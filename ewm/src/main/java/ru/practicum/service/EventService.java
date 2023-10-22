package ru.practicum.service;

import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.NewEventDto;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);
}
