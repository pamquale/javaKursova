package ua.edu.university.lab10.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.university.lab10.dto.CategoryDto;
import ua.edu.university.lab10.dto.CreateCategoryRequestDto;
import ua.edu.university.lab10.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequestDto requestDto) {
        return new ResponseEntity<>(categoryService.createCategory(requestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }
}