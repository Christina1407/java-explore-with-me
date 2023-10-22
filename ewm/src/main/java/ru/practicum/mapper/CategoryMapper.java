package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.Category;
import ru.practicum.model.dto.CategoryDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto map(Category category);

    Category map(CategoryDto categoryDto);

    List<CategoryDto> map(List<Category> categories);
}
