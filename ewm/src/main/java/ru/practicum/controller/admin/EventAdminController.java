package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.ParamsForAdmin;
import ru.practicum.model.dto.UpdateEventRequest;
import ru.practicum.model.enums.StateActionEnum;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping("{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable("eventId") @Min(1) Long eventId,
                                           @RequestBody @Valid UpdateEventRequest updateEventAdminRequest) {

        //Если меняется состояние события, то значения могут быть PUBLISH_EVENT or REJECT_EVENT
        StateActionEnum stateAction = updateEventAdminRequest.getStateAction();
        if (nonNull(stateAction) && !stateAction.equals(StateActionEnum.PUBLISH_EVENT) &&
                !stateAction.equals(StateActionEnum.REJECT_EVENT)) {
            throw new ConflictException(String.format("StateAction = %s. For admin endpoint StateAction must be " +
                    "PUBLISH_EVENT or REJECT_EVENT", stateAction), "For the requested operation the conditions are not met.");
        }
        log.info("Редактирование данных события id = {} администратором updateEventAdminRequest = {}", eventId, updateEventAdminRequest);
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }

    @GetMapping
    public List<EventFullDto> findEventsByAdmin(@Valid ParamsForAdmin params,
                                               @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                               @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {

        log.info("Получение администратором данных по параметрам = {}", params);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return eventService.findEventsByAdmin(params, pageable);
    }
}
