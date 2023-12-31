package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.dto.ParticipationRequestDto;
import ru.practicum.model.enums.StatusEnum;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "event", source = "event.id")
    ParticipationRequestDto map(Request request);

    @Mapping(target = "requester", source = "user")
    @Mapping(target = "event", source = "eventForRequest")
    @Mapping(target = "status", source = "statusEnum")
    @Mapping(target = "id", ignore = true)
    Request map(User user, Event eventForRequest, StatusEnum statusEnum);

    List<ParticipationRequestDto> map(List<Request> requests);
}
