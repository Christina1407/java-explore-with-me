package ru.practicum.controller.publicity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.GetEventsRequestParams;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
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
    public List<EventShortDto> getEvents(@Valid GetEventsRequestParams params,
                                         @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                         @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventService.getEvents(params, pageable);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventById(@PathVariable @Min(1) Long eventId,
                                      HttpServletRequest request) {
        log.info("Get event id = {}", eventId);
        String ip = request.getRemoteAddr();
        log.info("client ip: {}", ip);
        String requestURI = request.getRequestURI();
        log.info("endpoint path: {}", requestURI);
        return eventService.publicFindEventById(eventId, ip, requestURI);
    }
}
