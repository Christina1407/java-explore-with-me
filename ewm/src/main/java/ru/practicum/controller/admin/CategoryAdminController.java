package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Попытка сохранения новой категории {}", categoryDto);
        return categoryService.saveCategory(categoryDto);
    }

    @DeleteMapping("{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("categoryId") @Min(1) Long categoryId) {
        log.info("Попытка удаления категории id = {}", categoryId);
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping("{categoryId}")
    public CategoryDto updateCategory(@PathVariable("categoryId") @Min(1) Long categoryId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Попытка обновления category {} id = {}", categoryDto, categoryId);
        return categoryService.updateCategory(categoryDto, categoryId);
    }
}
