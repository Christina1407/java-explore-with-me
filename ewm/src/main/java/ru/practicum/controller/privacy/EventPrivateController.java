package ru.practicum.controller.privacy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.NewEventDto;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable("userId") @Min(1) Long userId,
                                  @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Попытка сохранения нового события {}", newEventDto);
        return eventService.saveEvent(userId, newEventDto);
    }
}
