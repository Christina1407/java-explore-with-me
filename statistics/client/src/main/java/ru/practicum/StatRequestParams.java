package ru.practicum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
//@StartBeforeEndDateValid
        //TODO перенести
public class StatRequestParams {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @DateTimeFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime start;
    @DateTimeFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime end;
    private List<String> uris;
    private boolean unique;
}
