package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.model.*;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.dto.NewEventDto;
import ru.practicum.model.dto.UpdateEventAdminRequest;
import ru.practicum.model.enums.StateEnum;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RequestMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "category", source = "existCategory")
    @Mapping(target = "location", source = "existLocation")
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "state", source = "stateEnum")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestModeration", defaultValue = "true")
    Event map(NewEventDto newEventDto, Category existCategory, Location existLocation, User user, StateEnum stateEnum);

    EventFullDto map(Event event, Long views);

    @Mapping(target = "category", source = "existCategory")
    @Mapping(target = "id", ignore = true)
    void updateByAdmin(UpdateEventAdminRequest updateEventAdminRequest, Category existCategory, @MappingTarget Event event);
    EventRequestStatusUpdateResult map(List<Request> confirmedRequests, List<Request> rejectedRequests);
}
