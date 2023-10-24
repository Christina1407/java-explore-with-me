package ru.practicum.validation;

import ru.practicum.model.dto.GetEventsRequestParams;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, GetEventsRequestParams> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(GetEventsRequestParams requestCriteria, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = requestCriteria.getRangeStart();
        LocalDateTime end = requestCriteria.getRangeEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}