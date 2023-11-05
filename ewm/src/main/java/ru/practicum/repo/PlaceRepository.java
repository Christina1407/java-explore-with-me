package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsByName(String placeName);
//    @Procedure("distance")
//    public double getDistance(@Param("lat1") double lat1, @Param("lon1") double lon1,
//                             @Param("lat2") double lat2, @Param("lon2") double lon2);
}
