package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.Place;
import ru.practicum.model.PlaceType;
import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.PlaceDto;

@Mapper(componentModel = "spring")
public interface PlaceMapper {
    PlaceDto map(Place place);
    @Mapping(target = "season", source = "newPlaceDto.season", defaultValue = "ALL")
    @Mapping(target = "type", source = "placeType")
    @Mapping(target = "name", source = "newPlaceDto.name")
    @Mapping(target = "id", ignore = true)
    Place map(NewPlaceDto newPlaceDto, PlaceType placeType);
}
