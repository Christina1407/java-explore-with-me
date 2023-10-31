package ru.practicum.controller.publicity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                              @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryService.getAllCategories(pageable);
    }

    @GetMapping("/{categoryId}")
    public CategoryDto findCategoryById(@PathVariable @Min(1) Long categoryId) {
        log.info("Get category id = {}", categoryId);
        return categoryService.findCategoryById(categoryId);
    }

}
