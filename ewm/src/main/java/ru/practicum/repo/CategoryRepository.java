package ru.practicum.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {

    boolean existsByName(String categoryName);

    boolean existsByNameAndIdNot(String categoryName, Long categoryId);
}
