package ru.practicum.validation;

import ru.practicum.model.StatRequestParams;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, StatRequestParams> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(StatRequestParams statRequestCriteria, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = statRequestCriteria.getStart();
        LocalDateTime end = statRequestCriteria.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}