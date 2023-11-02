package ru.practicum.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.enums.StateEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventFullDto {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = DATETIME_FORMAT)
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private StateEnum state;
    private String title;
    private int views;
}
