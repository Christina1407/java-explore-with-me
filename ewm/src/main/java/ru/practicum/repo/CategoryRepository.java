package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String categoryName);

    boolean existsByNameAndIdNot(String categoryName, Long categoryId);
}
