package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.model.Place;
import ru.practicum.model.PlaceType;
import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.PlaceDto;
import ru.practicum.model.dto.UpdatePlaceDto;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlaceMapper {
    PlaceDto map(Place place);

    @Mapping(target = "events", source = "publishedEvents")
    @Named(value = "published")
    PlaceDto mapWithPublishedEvents(Place place);

    @IterableMapping(qualifiedByName = "published")
    @Named(value = "PlaceList with published events")
    List<PlaceDto> mapWithPublishedEvents(List<Place> places);

    @IterableMapping(qualifiedByName = "all")
    @Named(value = "PlaceList with all events")
    List<PlaceDto> mapWithAllEvents(List<Place> places);

    @Named(value = "all")
    @Mapping(target = "events", source = "allEvents")
    PlaceDto mapWithAllEvents(Place place);

    @Mapping(target = "season", source = "newPlaceDto.season", defaultValue = "ALL")
    @Mapping(target = "type", source = "placeType")
    @Mapping(target = "name", source = "newPlaceDto.name")
    @Mapping(target = "id", ignore = true)
    Place map(NewPlaceDto newPlaceDto, PlaceType placeType);

    List<PlaceDto> map(List<Place> places);

    @Mapping(target = "type", source = "placeType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "updatePlaceDto.name")
    void update(UpdatePlaceDto updatePlaceDto, PlaceType placeType, @MappingTarget Place place);
}
