package ru.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.model.StatRequestParams;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.StatDtoResponse;

import java.util.List;

public interface StatsService {
    HitDto saveHit(HitDto hitDto);

    Page<StatDtoResponse> getStats(StatRequestParams params, Pageable pageable);
}
