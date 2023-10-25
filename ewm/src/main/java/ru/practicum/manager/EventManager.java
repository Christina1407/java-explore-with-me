package ru.practicum.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.enums.StatusEnum;
import ru.practicum.repo.EventRepository;

@Component
@AllArgsConstructor
public class EventManager {
    private final EventRepository eventRepository;

    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d was not found", eventId)));
    }
    public boolean isParticipantLimitHasBeenReached(Event event) {
        return event.getParticipantLimit() != 0 && event.getParticipantLimit() == event.getRequests().stream()
                .filter(request -> request.getStatus().equals(StatusEnum.CONFIRMED))
                .count();
    }

    public long getQuantityOfConfirmedRequests(Event event) {
        return event.getRequests().stream()
                .filter(request -> request.getStatus().equals(StatusEnum.CONFIRMED))
                .count();
    }
}
