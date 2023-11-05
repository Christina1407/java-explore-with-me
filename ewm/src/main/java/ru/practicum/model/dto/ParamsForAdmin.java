package ru.practicum.model.dto;


import lombok.*;
import ru.practicum.model.enums.StateEnum;

import java.util.List;

@Getter
@NoArgsConstructor
@Setter
@Builder
@AllArgsConstructor
@ToString(callSuper = true)
public class ParamsForAdmin extends GetEventsRequestParam {
    private List<Long> users;
    private List<StateEnum> states;
}
