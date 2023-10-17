package ru.practicum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.validation.StartBeforeEndDateValid;

import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@StartBeforeEndDateValid
public class StatRequestParams {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @DateTimeFormat(pattern = DATETIME_FORMAT)
    @Past(message = "start can't be in future")
    private LocalDateTime start;
    @DateTimeFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime end;
    private List<String> uris;
    private boolean unique;
}
