package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.manager.CategoryManager;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.repo.CategoryRepository;
import ru.practicum.repo.EventRepository;
import ru.practicum.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryManager categoryManager;

    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        //проверяем, что названия категории нет в базе
        existsByName(categoryDto.getName());
        Category category = categoryMapper.map(categoryDto);
        return categoryMapper.map(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryMapper.map(findCategoryById(categoryId));
        //проверка, что у категории нет привязанных событий
        //TODO проверить
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("The category is not empty", "For the requested operation the conditions are not met.");
        }
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findCategoryById(Long categoryId) {
        return categoryMapper.map(categoryManager.findCategoryById(categoryId));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category categoryForUpdate = categoryManager.findCategoryById(categoryId);
        //проверяем, что нового названия нет в базе. Исключаем id обновляемой категории для случая обновления с неизменёнными данными
        existsByNameAndCategoryIdNot(categoryDto.getName(), categoryId);
        categoryForUpdate.setName(categoryDto.getName());
        return categoryMapper.map(categoryForUpdate);
    }

    @Override
    public List<CategoryDto> getAllCategories(Pageable pageable) {
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        return categoryMapper.map(categories);
    }

    private void existsByNameAndCategoryIdNot(String categoryName, Long categoryId) {
        if (categoryRepository.existsByNameAndIdNot(categoryName, categoryId)) {
            throw new ConflictException("Constraint unique_category_name", "Integrity constraint has been violated.");
        }
    }
    private void existsByName(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            throw new ConflictException("Constraint unique_category_name", "Integrity constraint has been violated.");
        }
    }
}

