package ru.practicum.validation;


import ru.practicum.model.dto.GetEventsRequestParam;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, GetEventsRequestParam> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(GetEventsRequestParam value, ConstraintValidatorContext context) {
        LocalDateTime start = value.getRangeStart();
        LocalDateTime end = value.getRangeEnd();
        if (start != null && end != null) {
            return start.isBefore(end);
        }
        return true;
    }
}