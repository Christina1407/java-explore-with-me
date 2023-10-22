package ru.practicum.valid;



import ru.practicum.model.dto.NewEventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<EventDateAfterNowValid, NewEventDto> {
    public static final long MIN_HOURS_DIFFERENCE = 2;
    @Override
    public void initialize(EventDateAfterNowValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(NewEventDto eventDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime eventDate = eventDto.getEventDate();
        if (eventDate == null) {
            return false;
        }
        return eventDate.isAfter(LocalDateTime.now().plusHours(MIN_HOURS_DIFFERENCE));
    }
}