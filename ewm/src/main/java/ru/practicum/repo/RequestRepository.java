package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findByIdIn(List<Long> requestIds);

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);
}
