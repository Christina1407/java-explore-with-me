package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.controller.admin.CategoryAdminController;
import ru.practicum.controller.admin.UserController;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = {UserController.class, CategoryAdminController.class})
public class ExceptionsHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND.name(), "The required object was not found.", e.getMessage(), LocalDateTime.now());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(
               error -> message.append("Field: ").append(error.getField()).append(". Error: ")
                       .append(error.getDefaultMessage()).append(". Value: ").append(error.getRejectedValue()).append(". ")
        );
        return new ApiError(HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", message.toString().trim(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return new ApiError(HttpStatus.CONFLICT.name(), e.getReason(), e.getMessage(), LocalDateTime.now() );
    }

}
