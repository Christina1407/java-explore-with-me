package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.Place;

public interface PlaceRepository extends JpaRepository<Place, Long>, QuerydslPredicateExecutor<Place> {
    boolean existsByName(String placeName);

    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);
}
