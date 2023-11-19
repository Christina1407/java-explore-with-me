package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.ParamSearchPlace;
import ru.practicum.model.dto.PlaceDto;
import ru.practicum.model.dto.UpdatePlaceDto;

import java.util.List;

public interface PlaceService {
    PlaceDto savePlace(NewPlaceDto newPlaceDto);

    void deletePlace(Long placeId);

    PlaceDto findPlaceById(Long placeId, boolean isPublic);

    List<PlaceDto> findPlacesByCoordinates(Double lat, Double lon);

    PlaceDto updatePlace(UpdatePlaceDto updatePlaceDto, Long placeId);

    List<PlaceDto> findAllPlaces(Pageable pageable, boolean isPublic);

    List<PlaceDto> findPlacesWithFilters(ParamSearchPlace params, Pageable pageable);
}
