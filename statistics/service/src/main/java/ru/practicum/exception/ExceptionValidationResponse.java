package ru.practicum.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ExceptionValidationResponse extends ExceptionResponse {
    Map<String, String> errors;

    public ExceptionValidationResponse(Map<String, String> errors) {
        super("Ошибка валидации");
        this.errors = errors;
    }
}
