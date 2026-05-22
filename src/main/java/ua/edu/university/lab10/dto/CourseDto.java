package ua.edu.university.lab10.dto;
import lombok.Data;
@Data
public class CourseDto {
    private Long id;
    private String title;
    private String instructor;
    private Integer durationHours;
    private Long categoryId;
    private String categoryName;
}