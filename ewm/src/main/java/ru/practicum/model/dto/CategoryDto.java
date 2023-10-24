package ru.practicum.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CategoryDto {
    private Long id;
    @NotBlank(message = "name is empty")
    @Size(min = 1, max = 50, message = "name minLength = 1 maxLength = 50")
    private String name;
}
