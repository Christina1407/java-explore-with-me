package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.NewPlaceDto;
import ru.practicum.model.dto.PlaceDto;
import ru.practicum.model.dto.UpdatePlaceDto;
import ru.practicum.service.PlaceService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    @PatchMapping("{placeId}")
    public PlaceDto updatePlace(@PathVariable("placeId") @Min(1) Long placeId,
                                @RequestBody @Valid UpdatePlaceDto updatePlaceDto) {
        log.info("Попытка обновления place {} id = {}", updatePlaceDto, placeId);
        return placeService.updatePlace(updatePlaceDto, placeId);
    }

    @GetMapping("/{placeId}")
    public PlaceDto findPlaceById(@PathVariable @Min(1) Long placeId) {
        log.info("Get place id = {}", placeId);
        return placeService.findPlaceById(placeId, false);
    }

    @GetMapping
    public List<PlaceDto> findPlacesByCoordinates(@RequestParam(name = "lat") @NotNull @Min(-90) @Max(90) Double lat,
                                                  @RequestParam(name = "lon") @NotNull @Min(-180) @Max(180) Double lon) {
        log.info("Get places by coordinates lat = {} lon = {}", lat, lon);
        return placeService.findPlacesByCoordinates(lat, lon);
    }
}
