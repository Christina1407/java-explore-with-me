package ru.practicum.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE_USE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CheckDateValidator.class)
public @interface EventDateAfterNowValid {
    String message() default "EventDate can't be earlier than two hours from the current moment";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}