package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto saveCategory(CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto findCategoryById(Long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);

    List<CategoryDto> getAllCategories(Pageable pageable);
}
