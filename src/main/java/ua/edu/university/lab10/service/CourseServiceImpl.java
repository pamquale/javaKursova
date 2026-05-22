package ua.edu.university.lab10.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.university.lab10.dto.CourseDto;
import ua.edu.university.lab10.dto.CreateCourseRequestDto;
import ua.edu.university.lab10.exception.CourseAlreadyExistsException;
import ua.edu.university.lab10.model.Course;
import ua.edu.university.lab10.model.Category;
import ua.edu.university.lab10.repository.CourseRepository;
import ua.edu.university.lab10.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseNotificationProducer notificationProducer;

    @Override
    @Transactional
    public CourseDto createCourse(CreateCourseRequestDto requestDto) {
        if (courseRepository.existsByTitle(requestDto.getTitle())) {
            throw new CourseAlreadyExistsException("Курс із такою назвою вже існує на платформі!");
        }

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Категорію навчання не знайдено"));

        Course course = new Course();
        course.setTitle(requestDto.getTitle());
        course.setInstructor(requestDto.getInstructor());
        course.setDurationHours(requestDto.getDurationHours());
        course.setCategory(category);

        Course saved = courseRepository.save(course);
        CourseDto responseDto = mapToDto(saved);

        // Асинхронний тригер відправки події в чергу RabbitMQ
        notificationProducer.sendCourseCreationNotification(responseDto);

        return responseDto;
    }

    @Override
    public Page<CourseDto> getAllCourses(String search, Pageable pageable) {
        String searchParam = (search != null && !search.trim().isEmpty()) ? "%" + search.trim().toLowerCase() + "%" : null;
        return courseRepository.searchByTitleOrInstructor(searchParam, pageable).map(this::mapToDto);
    }

    @Override
    public List<CourseDto> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByCategoryId(categoryId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CourseDto getCourseById(Long id) {
        return courseRepository.findById(id).map(this::mapToDto).orElseThrow(() -> new EntityNotFoundException("Курс не знайдено"));
    }

    @Override
    @Transactional
    public CourseDto updateCourse(Long id, CreateCourseRequestDto requestDto) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Курс не знайдено"));
        
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Категорію навчання не знайдено"));
                
        course.setTitle(requestDto.getTitle());
        course.setInstructor(requestDto.getInstructor());
        course.setDurationHours(requestDto.getDurationHours());
        course.setCategory(category);
        
        return mapToDto(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) throw new EntityNotFoundException("Курс не знайдено");
        courseRepository.deleteById(id);
    }

    private CourseDto mapToDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setInstructor(course.getInstructor());
        dto.setDurationHours(course.getDurationHours());
        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
            dto.setCategoryName(course.getCategory().getName());
        }
        return dto;
    }
}