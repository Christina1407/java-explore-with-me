package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.Location;
import ru.practicum.model.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto map(Location location);

    Location map(LocationDto locationDto);
}
