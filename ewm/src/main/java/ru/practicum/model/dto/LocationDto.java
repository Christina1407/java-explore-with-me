package ru.practicum.model.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {
    @NotNull(message = "latitude is null")
    @Min(value = -90, message = "latitude cannot be less than -90")
    @Max(value = 90, message = "latitude cannot be more than 90")
    private Float lat;
    @NotNull(message = "longitude is null")
    @Min(value = -180, message = "longitude cannot be less than -180")
    @Max(value = 180, message = "longitude cannot be more than 180")
    private Float lon;
}
