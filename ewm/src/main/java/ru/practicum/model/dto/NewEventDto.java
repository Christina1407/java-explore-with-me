package ru.practicum.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.validation.EventDateAfterNowValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EventDateAfterNowValid
@ToString
public class NewEventDto {
    @NotBlank(message = "annotation is empty")
    @Size(min = 20, max = 2000, message = "annotation minLength = 20 maxLength = 2000")
    private String annotation;
    @NotNull(message = "categoryId is null")
    private Long category;
    @NotBlank(message = "annotation is empty")
    @Size(min = 20, max = 7000, message = "description minLength = 20 maxLength = 7000")
    private String description;
    @Future(message = "eventDate can't be in past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    @NotBlank(message = "title is empty")
    @Size(min = 3, max = 120, message = "title minLength = 3 maxLength = 120")
    private String title;

}
