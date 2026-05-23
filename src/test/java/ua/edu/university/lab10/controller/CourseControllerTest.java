package ua.edu.university.lab10.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.edu.university.lab10.dto.CreateCourseRequestDto;
import ua.edu.university.lab10.model.Course;
import ua.edu.university.lab10.model.Category;
import ua.edu.university.lab10.repository.CourseRepository;
import ua.edu.university.lab10.repository.CategoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Настройка MockMvc для выполнения HTTP-запросов
@Testcontainers // Активация управления контейнерами в тестах
@ActiveProfiles("test") // Использование изолированного профиля конфігурації
public class CourseControllerTest {

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }

    // Определение и запуск реальной БД PostgreSQL в Docker-контейнере
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("lms_test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    // Динамическое внедрение параметров подключения контейнера в контекст Spring
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Software Engineering");
        savedCategory = categoryRepository.save(category);
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Симуляция авторизованного администратора
    void givenValidCourse_whenCreateCourse_thenReturnCreatedAndVerifyInDb() throws Exception {
        // Given
        CreateCourseRequestDto requestDto = new CreateCourseRequestDto();
        requestDto.setTitle("Testcontainers Guide");
        requestDto.setInstructor("Bohdan Pohrebniak");
        requestDto.setDurationHours(250);
        requestDto.setCategoryId(savedCategory.getId());

        // When & Then
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Testcontainers Guide"));

        // Дополнительная проверка сохранения записи непосредственно в БД контейнера
        assertThat(courseRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(roles = "USER") // Симуляция обычного пользователя
    void givenExistingCourse_whenGetCourseById_thenReturnCourseDto() throws Exception {
        // Given
        Course course = new Course();
        course.setTitle("Integration Testing Essentials");
        course.setInstructor("Martin Fowler");
        course.setDurationHours(380);
        course.setCategory(savedCategory);
        Course savedCourse = courseRepository.save(course);

        // When & Then
        mockMvc.perform(get("/api/courses/" + savedCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Testing Essentials"))
                .andExpect(jsonPath("$.instructor").value("Martin Fowler"));
    }
}