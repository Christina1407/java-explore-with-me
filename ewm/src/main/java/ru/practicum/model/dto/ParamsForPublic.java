package ru.practicum.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class ParamsForPublic extends GetEventsRequestParam {
    private String text;
    private Boolean paid;
    private boolean onlyAvailable;
}
