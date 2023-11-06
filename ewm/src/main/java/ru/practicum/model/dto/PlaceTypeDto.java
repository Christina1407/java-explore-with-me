package ru.practicum.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceTypeDto {
    private Long id;
    private String name;
}
