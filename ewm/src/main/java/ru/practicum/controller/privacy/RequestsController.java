package ru.practicum.controller.privacy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestsController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@PathVariable("userId") @Min(1) Long userId,
                                               @RequestParam(name = "eventId") @Min(1) Long eventId) {
        log.info("Попытка сохранения нового запроса пользователя userId = {} на участие в событии eventId = {}",userId, eventId);
        return requestService.saveRequest(userId, eventId);
    }
}
