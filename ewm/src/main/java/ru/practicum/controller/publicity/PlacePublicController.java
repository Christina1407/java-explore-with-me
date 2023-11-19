package ru.practicum.controller.publicity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.PlaceDto;
import ru.practicum.service.PlaceService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/places")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PlacePublicController {
    private final PlaceService placeService;

    @GetMapping
    public List<PlaceDto> getAllPlaces(@RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                       @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return placeService.findAllPlaces(pageable, true);
    }

    @GetMapping("/{placeId}")
    public PlaceDto findPlaceById(@PathVariable @Min(1) Long placeId) {
        log.info("Get place id = {}", placeId);
        return placeService.findPlaceById(placeId, true);
    }
}
