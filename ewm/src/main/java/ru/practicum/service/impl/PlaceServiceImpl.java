package ru.practicum.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.PlaceManager;
import ru.practicum.mapper.PlaceMapper;
import ru.practicum.model.Place;
import ru.practicum.model.PlaceType;
import ru.practicum.model.QPlace;
import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.ParamSearchPlace;
import ru.practicum.model.dto.PlaceDto;
import ru.practicum.model.dto.UpdatePlaceDto;
import ru.practicum.model.enums.CompareEnum;
import ru.practicum.model.enums.SeasonEnum;
import ru.practicum.repo.PlaceRepository;
import ru.practicum.repo.PlaceTypeRepository;
import ru.practicum.service.PlaceService;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
        existsByCoordinates(newPlaceDto.getLatitude(), newPlaceDto.getLongitude());
        PlaceType placeType = getPlaceType(newPlaceDto.getType());
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
    public PlaceDto findPlaceById(Long placeId, boolean isPublic) {
        Place place = placeManager.findPlaceByIdOrThrow(placeId);
        return isPublic ?
                placeMapper.mapWithPublishedEvents(place) :
                placeMapper.mapWithAllEvents(place);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceDto> findPlacesByCoordinates(Double lat, Double lon) {
        return placeMapper.map(placeManager.findPlacesByCoordinates(lat, lon));
    }

    @Override
    public PlaceDto updatePlace(UpdatePlaceDto updatePlaceDto, Long placeId) {
        Place placeForUpdate = placeManager.findPlaceByIdOrThrow(placeId);
        if (nonNull(updatePlaceDto.getName())) {
            existsByName(updatePlaceDto.getName());
        }
        PlaceType placeType = null;
        if (nonNull(updatePlaceDto.getType())) {
            placeType = getPlaceType(updatePlaceDto.getType());
        }
        placeMapper.update(updatePlaceDto, placeType, placeForUpdate);
        return placeMapper.map(placeForUpdate);
    }

    @Override
    public List<PlaceDto> findAllPlaces(Pageable pageable, boolean isPublic) {
        List<Place> places = placeRepository.findAll(pageable).getContent();
        return (isPublic) ?
                placeMapper.mapWithPublishedEvents(places) :
                placeMapper.mapWithAllEvents(places);
    }

    @Override
    public List<PlaceDto> findPlacesWithFilters(ParamSearchPlace params, Pageable pageable) {
        validateParams(params);
        List<Place> places = placeRepository.findAll(getPredicateByParams(params), pageable).getContent();
        return placeMapper.mapWithAllEvents(places);
    }

    private void validateParams(ParamSearchPlace params) {
        if (nonNull(params.getComparison()) && isNull(params.getRadius())) {
            log.error("There is no place's radius in request params {}", params);
            throw new ConflictException("There is no place's radius in request params", "For the requested operation the conditions are not met.");
        }
        if (isNull(params.getComparison())) {
            params.setComparison(CompareEnum.EQUAL);
        }
        if (isNull(params.getSeason())) {
            params.setSeason(SeasonEnum.ALL);
        }
    }

    private void existsByName(String placeName) {
        if (placeRepository.existsByName(placeName)) {
            log.error("Constraint unique_place_name {}", placeName);
            throw new ConflictException(String.format("Constraint unique_place_name %s", placeName), "Integrity constraint has been violated.");
        }
    }

    private void existsByCoordinates(Double latitude, Double longitude) {
        if (placeRepository.existsByLatitudeAndLongitude(latitude, longitude)) {
            log.error("The place with coordinates lat = {} and lon = {} already exists in the database", latitude, longitude);
            throw new ConflictException(String.format("The place with coordinates lat = %f and lon = %f already exists in the database", latitude, latitude),
                    "Integrity constraint has been violated.");
        }
    }

    private PlaceType getPlaceType(Long placeTypeId) {
        return placeTypeRepository.findById(placeTypeId).orElseThrow(() ->
                new NotFoundException(String.format("PlaceType with id = %d was not found", placeTypeId)));
    }

    private Predicate getPredicateByParams(ParamSearchPlace params) {
        BooleanBuilder where = new BooleanBuilder();
        if (nonNull(params.getText())) {
            where.and(QPlace.place.name.containsIgnoreCase(params.getText())
                    .or(QPlace.place.feature.containsIgnoreCase(params.getText())));
        }
        if (!CollectionUtils.isEmpty(params.getTypes())) {
            where.and(QPlace.place.type.id.in(params.getTypes()));
        }
        if (nonNull(params.getRadius())) {
            if (params.getComparison().equals(CompareEnum.EQUAL)) {
                where.and(QPlace.place.radius.eq(params.getRadius()));
            } else if (params.getComparison().equals(CompareEnum.GREATER_THAN)) {
                where.and(QPlace.place.radius.gt(params.getRadius()));
            } else if (params.getComparison().equals(CompareEnum.LESS_THAN)) {
                where.and(QPlace.place.radius.lt(params.getRadius()));
            }
        }
        if (nonNull(params.getSeason())) {
            where.and(QPlace.place.season.eq(params.getSeason()));
        }
        return where;
    }

}
