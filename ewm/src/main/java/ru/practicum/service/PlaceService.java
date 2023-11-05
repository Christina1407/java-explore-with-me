package ru.practicum.service;

import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.PlaceDto;

import java.util.List;

public interface PlaceService {
    PlaceDto savePlace(NewPlaceDto newPlaceDto);

    void deletePlace(Long placeId);

    PlaceDto findPlaceById(Long placeId);

    List<PlaceDto> findPlacesByCoordinates(Double lat, Double lon);
}
