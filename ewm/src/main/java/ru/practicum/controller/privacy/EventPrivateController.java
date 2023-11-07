package ru.practicum.controller.privacy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.model.dto.*;
import ru.practicum.model.enums.StateActionEnum;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findInitiatorEvents(@PathVariable("userId") @Min(1) Long userId,
                                                   @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Get events by user id = {}", userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventService.findInitiatorEvents(userId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable("userId") @Min(1) Long userId,
                                  @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Попытка сохранения нового события {}", newEventDto);
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findInitiatorEventById(@PathVariable("userId") @Min(1) Long userId,
                                               @PathVariable("eventId") @Min(1) Long eventId,
                                               HttpServletRequest request) {
        log.info("Get event id = {} by user id = {}", eventId, userId);
        return eventService.findInitiatorEventById(userId, eventId);
    }

    @PatchMapping("{eventId}")
    public EventFullDto updateEventByInitiator(@PathVariable("userId") @Min(1) Long userId,
                                               @PathVariable("eventId") @Min(1) Long eventId,
                                               HttpServletRequest httpServletRequest,
                                               @RequestBody @Valid UpdateEventRequest updateEventUserRequest) {

        //Если меняется состояние события, то значения могут быть SEND_TO_REVIEW или CANCEL_REVIEW
        StateActionEnum stateAction = updateEventUserRequest.getStateAction();
        if (nonNull(stateAction) && !stateAction.equals(StateActionEnum.SEND_TO_REVIEW) &&
                !stateAction.equals(StateActionEnum.CANCEL_REVIEW)) {
            throw new ConflictException(String.format("StateAction = %s. For user endpoint StateAction must be " +
                    "SEND_TO_REVIEW or CANCEL_REVIEW", stateAction), "For the requested operation the conditions are not met.");
        }

        log.info("Изменение события id = {} {} пользователем id = {}",
                eventId, updateEventUserRequest, userId);
        return eventService.updateEventByInitiator(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findRequestsByInitiatorOfEvent(@PathVariable("userId") @Min(1) Long userId,
                                                                        @PathVariable("eventId") @Min(1) Long eventId) {
        log.info("Получение информации о запросах на участие в событии id = {} текущего пользователя id = {}", eventId, userId);
        return eventService.findRequestsByInitiatorOfEvent(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult confirmOrRejectRequestsByInitiatorOfEvent(
            @PathVariable("userId") @Min(1) Long userId,
            @PathVariable("eventId") @Min(1) Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Изменение статуса заявок {} на участие в событии id = {} пользователем id = {}",
                eventRequestStatusUpdateRequest, eventId, userId);
        return eventService.confirmOrRejectRequestsByInitiatorOfEvent(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
