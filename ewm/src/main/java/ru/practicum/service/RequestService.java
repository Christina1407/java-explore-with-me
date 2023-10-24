package ru.practicum.service;

import ru.practicum.model.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto saveRequest(Long userId, Long eventId);
}
