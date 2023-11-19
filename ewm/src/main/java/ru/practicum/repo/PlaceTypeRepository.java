package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.PlaceType;

public interface PlaceTypeRepository extends JpaRepository<PlaceType, Long> {
}
