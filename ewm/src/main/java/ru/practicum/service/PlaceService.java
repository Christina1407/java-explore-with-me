package ru.practicum.service;

import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.PlaceDto;

public interface PlaceService {
    PlaceDto savePlace(NewPlaceDto newPlaceDto);

    void deletePlace(Long placeId);

    PlaceDto findPlaceById(Long placeId);
}
