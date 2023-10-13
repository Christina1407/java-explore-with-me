package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.StatRequestParams;
import ru.practicum.model.dto.StatDtoResponse;
import ru.practicum.model.dto.HitDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveHit(@RequestBody @Valid HitDto hitDto) {
        log.info("Сохранение информации о том, что к эндпоинту был запрос {}", hitDto);
        return statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public Page<StatDtoResponse> getStats(@Valid StatRequestParams params,
                                          @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                          @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return statsService.getStats(params, pageable);
    }
}
