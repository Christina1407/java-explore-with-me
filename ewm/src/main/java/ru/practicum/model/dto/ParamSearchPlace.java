package ru.practicum.model.dto;


import lombok.*;
import ru.practicum.model.enums.CompareEnum;
import ru.practicum.model.enums.SeasonEnum;

import javax.validation.constraints.Min;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
@Builder
@AllArgsConstructor
@ToString
public class ParamSearchPlace {
    private String text;
    private List<Long> types;
    CompareEnum compareRadius;
    @Min(1)
    Integer radius;
    SeasonEnum season;
}
