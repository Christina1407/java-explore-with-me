package ru.practicum.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceTypeDto {
    private Long id;
    @NotBlank(message = "name of placeType is empty")
    private String name;
}
