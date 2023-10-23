package ru.practicum.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repo.CategoryRepository;

@Component
@AllArgsConstructor
public class CategoryManager {
    private final CategoryRepository categoryRepository;

    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id = %d was not found", categoryId)));
    }
}
