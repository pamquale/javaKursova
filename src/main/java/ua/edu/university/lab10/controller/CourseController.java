package ua.edu.university.lab10.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.edu.university.lab10.dto.CourseDto;
import ua.edu.university.lab10.dto.CreateCourseRequestDto;
import ua.edu.university.lab10.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CreateCourseRequestDto requestDto) {
        return new ResponseEntity<>(courseService.createCourse(requestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CourseDto>> getAllCourses(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(search, pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseDto>> getCoursesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id, @Valid @RequestBody CreateCourseRequestDto requestDto) {
        return ResponseEntity.ok(courseService.updateCourse(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}