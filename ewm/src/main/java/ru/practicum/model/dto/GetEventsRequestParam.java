package ru.practicum.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.validation.StartBeforeEndDateValid;

import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@NoArgsConstructor
@StartBeforeEndDateValid
@Setter
public abstract class GetEventsRequestParam {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private List<Long> categories;
    @DateTimeFormat(pattern = DATETIME_FORMAT)
    @PastOrPresent(message = "start can't be in future")
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime rangeEnd;

}
