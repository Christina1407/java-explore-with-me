package ru.practicum.model.dto;

import lombok.*;
import ru.practicum.model.enums.SeasonEnum;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class NewPlaceDto {

    @NotNull(message = "latitude is null")
    @Min(value = -90, message = "latitude cannot be less than -90")
    @Max(value = 90, message = "latitude cannot be more than 90")
    private BigDecimal latitude;
    @NotNull(message = "longitude is null")
    @Min(value = -180, message = "longitude cannot be less than -180")
    @Max(value = 180, message = "longitude cannot be more than 180")
    private BigDecimal longitude;
    @NotNull(message = "radius is null")
    @Min(value = 1, message = "radius cannot be less than 1 m")
    private Integer radius;
    @NotBlank(message = "name of place is empty")
    @Size(min = 1, max = 100, message = "name minLength = 1 maxLength = 100")
    private String name;
    @NotNull
    private Long type;
    private SeasonEnum season;
    @Size(max = 100, message = "feature maxLength = 100")
    private String feature;
}
