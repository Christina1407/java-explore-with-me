package ru.practicum.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.repo.EventRepository;

@Component
@AllArgsConstructor
public class EventManager {
    private final EventRepository eventRepository;

    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d was not found", eventId)));
    }

}
