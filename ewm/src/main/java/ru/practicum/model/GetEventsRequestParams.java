package ru.practicum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.validation.StartBeforeEndDateValid;

import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@StartBeforeEndDateValid
public class GetEventsRequestParams {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String text;
    private List<Long> categories;
    private Boolean paid;
    @DateTimeFormat(pattern = DATETIME_FORMAT)
    @PastOrPresent(message = "start can't be in future")
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime rangeEnd;
    private boolean onlyAvailable;
    private SortEnum sort;

}
