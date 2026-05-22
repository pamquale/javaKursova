package ua.edu.university.lab10.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.edu.university.lab10.dto.CourseDto;
import ua.edu.university.lab10.dto.CreateCourseRequestDto;
import ua.edu.university.lab10.exception.CourseAlreadyExistsException;
import ua.edu.university.lab10.model.Category;
import ua.edu.university.lab10.repository.CourseRepository;
import ua.edu.university.lab10.repository.CategoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class CourseServiceIntegrationTest {

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("library_service_test_db")
            .withUsername("service_user")
            .withPassword("service_pass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Databases");
        savedCategory = categoryRepository.save(category);
    }

    @Test
    void whenCreateCourse_thenCourseIsSuccessfullySavedInContainerDb() {
        // Given
        CreateCourseRequestDto dto = new CreateCourseRequestDto();
        dto.setTitle("High-Performance PostgreSQL");
        dto.setInstructor("Vlad Mihalcea");
        dto.setDurationHours(520);
        dto.setCategoryId(savedCategory.getId());

        // When
        CourseDto savedDto = courseService.createCourse(dto);

        // Then
        assertThat(savedDto.getId()).isNotNull();
        assertThat(courseRepository.findById(savedDto.getId())).isPresent();
    }

    @Test
    void whenGetCourseById_thenReturnCorrectMappedDto() {
        // Given
        CreateCourseRequestDto dto = new CreateCourseRequestDto();
        dto.setTitle("Spring Boot Patterns");
        dto.setInstructor("Craig Walls");
        dto.setDurationHours(450);
        dto.setCategoryId(savedCategory.getId());

        CourseDto savedDto = courseService.createCourse(dto);

        // When
        CourseDto foundDto = courseService.getCourseById(savedDto.getId());

        // Then
        assertThat(foundDto.getTitle()).isEqualTo("Spring Boot Patterns");
        assertThat(foundDto.getCategoryId()).isEqualTo(savedCategory.getId());
    }

    @Test
    void givenDuplicateTitle_whenCreateCourse_thenThrowExceptionAndVerifyTransactionRollback() {
        // Given
        CreateCourseRequestDto dto = new CreateCourseRequestDto();
        dto.setTitle("Transactional Java Systems");
        dto.setInstructor("Juergen Hoeller");
        dto.setDurationHours(310);
        dto.setCategoryId(savedCategory.getId());

        // Первый раз курс создается успешно
        courseService.createCourse(dto);

        // When & Then: Повторная попытка должна вызвать бизнес-исключение дубликата названия
        assertThatThrownBy(() -> courseService.createCourse(dto))
                .isInstanceOf(CourseAlreadyExistsException.class);
        
        // Проверка транзакционности: В базе должна остаться строго 1 запись, вторая полностью откатилась (Rollback)
        assertThat(courseRepository.findAll()).hasSize(1);
    }
}