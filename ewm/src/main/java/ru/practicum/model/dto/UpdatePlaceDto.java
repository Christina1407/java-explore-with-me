package ru.practicum.model.dto;

import lombok.*;
import ru.practicum.model.enums.SeasonEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UpdatePlaceDto {
    @Min(value = -90, message = "latitude cannot be less than -90")
    @Max(value = 90, message = "latitude cannot be more than 90")
    private Double latitude;
    @Min(value = -180, message = "longitude cannot be less than -180")
    @Max(value = 180, message = "longitude cannot be more than 180")
    private Double longitude;
    @Min(value = 1, message = "radius cannot be less than 1 m")
    private Integer radius;
    @Size(min = 1, max = 100, message = "name minLength = 1 maxLength = 100")
    private String name;
    private Long type;
    private SeasonEnum season;
    @Size(max = 100, message = "feature maxLength = 100")
    private String feature;
}
