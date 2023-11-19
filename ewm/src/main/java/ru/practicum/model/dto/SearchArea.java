package ru.practicum.model.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor
@Setter
@Builder
@AllArgsConstructor
@ToString
public class SearchArea {
    @Min(value = -90, message = "latitude cannot be less than -90")
    @Max(value = 90, message = "latitude cannot be more than 90")
    private Double lat;
    @Min(value = -180, message = "longitude cannot be less than -180")
    @Max(value = 180, message = "longitude cannot be more than 180")
    private Double lon;
    @Min(value = 1, message = "radius cannot be less than 1 m")
    private Integer radius;
}
