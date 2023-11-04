package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.PlaceDto;
import ru.practicum.service.PlaceService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/admin/places")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PlaceAdminController {
    private final PlaceService placeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceDto savePlace(@RequestBody @Valid NewPlaceDto newPlaceDto) {
        log.info("Попытка сохранения нового места {}", newPlaceDto);
        return placeService.savePlace(newPlaceDto);
    }

    @DeleteMapping("{placeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlace(@PathVariable("placeId") @Min(1) Long placeId) {
        log.info("Попытка удаления места id = {}", placeId);
        placeService.deletePlace(placeId);
    }

    @GetMapping("/{placeId}")
    public PlaceDto findPlaceById(@PathVariable @Min(1) Long placeId) {
        log.info("Get place id = {}", placeId);
        return placeService.findPlaceById(placeId);
    }
}
