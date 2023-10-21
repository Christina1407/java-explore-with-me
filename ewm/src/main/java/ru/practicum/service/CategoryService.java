package ru.practicum.service;

import ru.practicum.model.dto.CategoryDto;

public interface CategoryService {
    CategoryDto saveCategory(CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto findCategoryById(Long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);
}
