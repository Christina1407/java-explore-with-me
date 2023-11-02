package ru.practicum.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.model.enums.StateActionEnum;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UpdateEventRequest {

    @Size(min = 20, max = 2000, message = "annotation minLength = 20 maxLength = 2000")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "description minLength = 20 maxLength = 7000")
    private String description;
    @Future(message = "eventDate can't be in past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActionEnum stateAction;
    @Size(min = 3, max = 120, message = "title minLength = 3 maxLength = 120")
    private String title;
}
