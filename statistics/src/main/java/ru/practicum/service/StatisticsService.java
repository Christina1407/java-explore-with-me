package ru.practicum.service;

import ru.practicum.model.dto.HitDto;

public interface StatisticsService {
    HitDto saveHit(HitDto hitDto);
}
