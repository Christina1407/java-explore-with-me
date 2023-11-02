package ru.practicum.service;

import ru.practicum.model.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto saveRequest(Long userId, Long eventId);


    List<ParticipationRequestDto> findRequests(Long userId);

    ParticipationRequestDto cancelRequestByRequester(Long userId, Long requestId);
}
