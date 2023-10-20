package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.controller.admin.UserController;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = {UserController.class})
public class ExceptionsHandler {
//TODO ApiError
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final NotFoundException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND.name(), "The required object was not found.", e.getMessage(), LocalDateTime.now());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", e.getMessage(), LocalDateTime.now());
    }


}
