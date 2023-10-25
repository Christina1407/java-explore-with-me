package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.controller.admin.CategoryAdminController;
import ru.practicum.controller.admin.EventAdminController;
import ru.practicum.controller.admin.UserController;
import ru.practicum.controller.privacy.EventPrivateController;
import ru.practicum.controller.privacy.RequestsController;
import ru.practicum.controller.publicity.CategoryPublicController;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = {UserController.class, CategoryAdminController.class, CategoryPublicController.class,
        EventPrivateController.class, EventPrivateController.class, RequestsController.class, EventAdminController.class, RequestsController.class})
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
        if (message.toString().isBlank()) {
            e.getBindingResult().getAllErrors().forEach(
                    error -> message.append(error.getDefaultMessage())
            );
        }
        return new ApiError(HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", message.toString().trim(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return new ApiError(HttpStatus.CONFLICT.name(), e.getReason(), e.getMessage(), LocalDateTime.now() );
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final Exception e) {
        return new ApiError(HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", e.getMessage(), LocalDateTime.now() );
    }

}
