package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.StatRequestParams;
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
    //List, c Page не проходят тесты
    public List<StatDtoResponse> getStats(@Valid StatRequestParams params,
                                          @RequestParam(name = "from", defaultValue = "0") @Min(0) int page,
                                          @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return statsService.getStats(params, pageable);
    }
}
