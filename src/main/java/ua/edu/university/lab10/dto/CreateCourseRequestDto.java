package ua.edu.university.lab10.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCourseRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Instructor is required")
    private String instructor;

    @NotNull(message = "Duration in hours is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer durationHours;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}