package ru.practicum.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.enums.SeasonEnum;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceDto {

    private Long id;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer radius;
    private String name;
    private PlaceTypeDto type;
    private SeasonEnum season;
    private String feature;
    private List<EventShortDto> events;
}
