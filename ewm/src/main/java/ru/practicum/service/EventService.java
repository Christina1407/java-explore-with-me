package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.model.dto.*;

import java.util.List;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);


    EventFullDto findInitiatorEventById(Long userId, Long eventId);

    EventFullDto findEventByIdPublic(Long eventId, String ip, String requestURI);

    List<EventShortDto> findEventsPublic(ParamsForPublic params, Pageable pageable);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateEventAdminRequest);

    EventRequestStatusUpdateResult confirmOrRejectRequestsByInitiatorOfEvent(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> findRequestsByInitiatorOfEvent(Long userId, Long eventId);

    EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventRequest updateEventUserRequest);

    List<EventShortDto> findInitiatorEvents(Long userId, Pageable pageable);

    List<EventFullDto> findEventsByAdmin(ParamsForAdmin params, Pageable pageable);
}
