package ua.edu.university.lab10.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.edu.university.lab10.dto.CourseDto;
import ua.edu.university.lab10.dto.CreateCourseRequestDto;

import java.util.List;

public interface CourseService {
    CourseDto createCourse(CreateCourseRequestDto requestDto);
    Page<CourseDto> getAllCourses(String search, Pageable pageable);
    List<CourseDto> getCoursesByCategory(Long categoryId);
    CourseDto getCourseById(Long id);
    CourseDto updateCourse(Long id, CreateCourseRequestDto requestDto);
    void deleteCourse(Long id);
}