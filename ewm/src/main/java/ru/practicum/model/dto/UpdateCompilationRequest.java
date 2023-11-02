package ru.practicum.model.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    @Size(min = 2, max = 50, message = "title minLength = 1 maxLength = 50")
    private String title;
}
