package ru.practicum.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatDtoResponse {
    private String app;
    private String uri;
    private Long hits;
}
