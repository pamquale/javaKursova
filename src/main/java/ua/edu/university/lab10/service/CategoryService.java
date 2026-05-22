package ua.edu.university.lab10.service;

import ua.edu.university.lab10.dto.CategoryDto;
import ua.edu.university.lab10.dto.CreateCategoryRequestDto;
import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CreateCategoryRequestDto requestDto);
    List<CategoryDto> getAllCategories();
}