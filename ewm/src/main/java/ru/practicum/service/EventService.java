package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.model.dto.*;

import java.util.List;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);


    EventFullDto findMyEventById(Long userId, Long eventId, String requestURI);

    EventFullDto publicFindEventById(Long eventId, String ip, String requestURI);

    List<EventShortDto> getEvents(GetEventsRequestParams params, Pageable pageable);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
