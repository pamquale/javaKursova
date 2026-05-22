package ua.edu.university.lab10.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.university.lab10.dto.CategoryDto;
import ua.edu.university.lab10.dto.CreateCategoryRequestDto;
import ua.edu.university.lab10.exception.CourseAlreadyExistsException;
import ua.edu.university.lab10.model.Category;
import ua.edu.university.lab10.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new CourseAlreadyExistsException("Категория с таким названием уже существует!");
        }
        Category category = new Category();
        category.setName(requestDto.getName());
        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private CategoryDto mapToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}