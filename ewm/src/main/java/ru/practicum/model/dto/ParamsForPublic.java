package ru.practicum.model.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@Setter
@Builder
@AllArgsConstructor
@ToString(callSuper = true)
public class ParamsForPublic extends GetEventsRequestParam {
    private String text;
    private Boolean paid;
    private boolean onlyAvailable;
}
