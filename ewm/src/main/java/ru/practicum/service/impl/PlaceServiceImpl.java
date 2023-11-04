package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.PlaceManager;
import ru.practicum.mapper.PlaceMapper;
import ru.practicum.model.Place;
import ru.practicum.model.PlaceType;
import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.PlaceDto;
import ru.practicum.repo.PlaceRepository;
import ru.practicum.repo.PlaceTypeRepository;
import ru.practicum.service.PlaceService;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlaceServiceImpl implements PlaceService {
    private final PlaceMapper placeMapper;
    private final PlaceRepository placeRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final PlaceManager placeManager;

    @Override
    public PlaceDto savePlace(NewPlaceDto newPlaceDto) {
        existsByName(newPlaceDto.getName());
        PlaceType placeType = placeTypeRepository.findById(newPlaceDto.getType()).orElseThrow(() ->
                new NotFoundException(String.format("PlaceType with id = %d was not found", newPlaceDto.getType())));
        Place place = placeMapper.map(newPlaceDto, placeType);
        return placeMapper.map(placeRepository.save(place));
    }

    @Override
    public void deletePlace(Long placeId) {
        Place place = placeManager.findPlaceByIdOrThrow(placeId);
        //проверка, что у места нет привязанных событий
        if (!place.getAllEvents().isEmpty()) {
            log.error("Attempt to delete place id = {}. The place has events", placeId);
            throw new ConflictException(String.format("The place id = %d has events", placeId),
                    "For the requested operation the conditions are not met.");
        }
        placeRepository.delete(place);
    }

    @Override
    @Transactional(readOnly = true)
    public PlaceDto findPlaceById(Long placeId) {
        Place place = placeManager.findPlaceByIdOrThrow(placeId);
        return placeMapper.map(place);
    }

    private void existsByName(String placeName) {
        if (placeRepository.existsByName(placeName)) {
            throw new ConflictException("Constraint unique_place_name", "Integrity constraint has been violated.");
        }
    }
}
