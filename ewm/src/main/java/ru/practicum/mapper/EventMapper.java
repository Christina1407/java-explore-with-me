package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.model.*;
import ru.practicum.model.dto.*;
import ru.practicum.model.enums.StateEnum;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RequestMapper.class, PlaceMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "category", source = "existCategory")
    @Mapping(target = "lat", source = "newEventDto.location.lat")
    @Mapping(target = "lon", source = "newEventDto.location.lon")
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "state", source = "stateEnum")
    @Mapping(target = "places", source = "places")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestModeration", defaultValue = "true")
    Event map(NewEventDto newEventDto, Category existCategory, User user, StateEnum stateEnum, List<Place> places);

    @Mapping(target = "location.lat", source = "lat")
    @Mapping(target = "location.lon", source = "lon")
    EventFullDto map(Event event);

    @Mapping(target = "category", source = "existCategory")
    @Mapping(target = "id", ignore = true)
    void update(UpdateEventRequest updateEventRequest, Category existCategory, @MappingTarget Event event);

    EventRequestStatusUpdateResult map(List<Request> confirmedRequests, List<Request> rejectedRequests);

    List<EventShortDto> mapToEventShortDtoList(List<Event> events);

    List<EventFullDto> mapToEventFullDtoList(List<Event> events);
}
