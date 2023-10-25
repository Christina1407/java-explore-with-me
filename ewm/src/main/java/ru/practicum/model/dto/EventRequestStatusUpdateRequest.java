package ru.practicum.model.dto;

import lombok.*;
import ru.practicum.model.enums.StatusEnum;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private StatusEnum status;
}
