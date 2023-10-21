package ru.practicum.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.Event;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {

    boolean existsByCategoryId(Long categoryId);
}
