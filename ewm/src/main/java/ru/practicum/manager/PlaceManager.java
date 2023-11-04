package ru.practicum.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Place;
import ru.practicum.repo.PlaceRepository;

import java.util.List;

import static java.util.Objects.nonNull;

@Component
@AllArgsConstructor
public class PlaceManager {
    private final PlaceRepository placeRepository;

    public Place findPlaceByIdOrThrow(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow(() -> new NotFoundException(String.format("Place with id =  %d was not found", placeId)));
    }
    public List<Place> findPlaces(List<Long> placeIds) {
        if (nonNull(placeIds)) {
            return placeRepository.findAllById(placeIds);
        }
        return null;
    }
}
