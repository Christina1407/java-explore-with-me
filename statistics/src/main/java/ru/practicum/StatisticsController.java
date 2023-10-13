package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.model.dto.HitDto;
import ru.practicum.service.StatisticsService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveHit(@RequestBody @Valid HitDto hitDto) {
        log.info("Сохранение информации о том, что к эндпоинту был запрос {}", hitDto);
        return statisticsService.saveHit(hitDto);
    }
}
