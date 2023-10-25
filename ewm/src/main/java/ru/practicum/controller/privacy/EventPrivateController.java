package ru.practicum.controller.privacy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.dto.NewEventDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("/{eventId}")
    public EventFullDto findMyEventById(@PathVariable("userId") @Min(1) Long userId,
                                        @PathVariable @Min(1) Long eventId,
                                        HttpServletRequest request) {
        log.info("Get event id = {} by user id = {}", eventId, userId);
        String requestURI = request.getRequestURI();
        log.info("endpoint path: {}", requestURI);
        return eventService.findMyEventById(userId, eventId, requestURI);
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult confirmOrRejectRequests(@PathVariable("userId") @Min(1) Long userId,
                                                                  @PathVariable("eventId") @Min(1) Long eventId,
                                                                  @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Изменение статуса заявок {} на участие в событии id = {} пользователем id = {}",
                eventRequestStatusUpdateRequest, eventId, userId);
        return eventService.confirmOrRejectRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
