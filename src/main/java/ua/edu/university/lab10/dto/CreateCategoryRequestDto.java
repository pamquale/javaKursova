package ua.edu.university.lab10.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequestDto {
    @NotBlank(message = "Название категории не может быть пустым")
    private String name;
}