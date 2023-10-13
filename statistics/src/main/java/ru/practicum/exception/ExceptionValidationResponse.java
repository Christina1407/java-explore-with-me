package ru.practicum.exception;

import lombok.Getter;
import ru.practicum.exception.ExceptionResponse;

import java.util.Map;

@Getter
public class ExceptionValidationResponse extends ExceptionResponse {
    Map<String, String> errors;

    public ExceptionValidationResponse(Map<String, String> errors) {
        super("Ошибка валидации");
        this.errors = errors;
    }
}
