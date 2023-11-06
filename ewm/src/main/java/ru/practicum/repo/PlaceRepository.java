package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsByName(String placeName);
}
