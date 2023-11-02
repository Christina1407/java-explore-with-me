package ru.practicum.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.model.enums.StatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private Long id;
    @JsonFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private StatusEnum status;
}
