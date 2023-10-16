package ru.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.HitDto;
import ru.practicum.StatDtoResponse;
import ru.practicum.model.StatRequestParams;

import java.util.List;

public interface StatsService {
    HitDto saveHit(HitDto hitDto);

    List<StatDtoResponse> getStats(StatRequestParams params, Pageable pageable);
}
