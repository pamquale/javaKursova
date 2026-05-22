package ua.edu.university.lab10.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.edu.university.lab10.model.Course;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitle(String title);

    List<Course> findByCategoryId(Long categoryId);

    // Інтегровано явне приведення типів CAST для стабільної роботи з PostgreSQL у Docker
    @Query("SELECT c FROM Course c WHERE CAST(:search AS string) IS NULL OR " +
            "LOWER(c.title) LIKE LOWER(CAST(:search AS string)) OR " +
            "LOWER(c.instructor) LIKE LOWER(CAST(:search AS string))")
    Page<Course> searchByTitleOrInstructor(@Param("search") String search, Pageable pageable);
}