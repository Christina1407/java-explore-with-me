package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.model.GetEventsRequestParams;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.model.dto.NewEventDto;

import java.util.List;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);


    EventFullDto findMyEventById(Long userId, Long eventId, String requestURI);

    EventFullDto publicFindEventById(Long eventId, String ip, String requestURI);

    List<EventShortDto> getEvents(GetEventsRequestParams params, Pageable pageable);
}
