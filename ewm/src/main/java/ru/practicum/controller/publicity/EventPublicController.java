package ru.practicum.controller.publicity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.model.dto.ParamsForPublic;
import ru.practicum.model.dto.SearchArea;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findEventsPublic(@Valid ParamsForPublic params,
                                                @Valid SearchArea searchArea,
                                                @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
                                                @RequestParam(name = "sort", defaultValue = "EVENT_DATE") SortEnum sort) {
        log.info("Get events params {} searchArea = {} from = {} size = {} sort = {}", params, searchArea, from, size, sort);
        return eventService.findEventsPublic(params, searchArea, from, size, sort);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventById(@PathVariable @Min(1) Long eventId) {
        log.info("Get event id = {}", eventId);
        return eventService.findEventByIdPublic(eventId);
    }
}
