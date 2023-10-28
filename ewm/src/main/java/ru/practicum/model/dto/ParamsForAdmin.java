package ru.practicum.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.enums.StateEnum;

import java.util.List;
@Getter
@NoArgsConstructor
@Setter
public class ParamsForAdmin extends GetEventsRequestParam{
    private List<Long> users;
    private List<StateEnum> states;
}
