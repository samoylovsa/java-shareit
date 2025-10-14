package ru.practicum.shareit.booking.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingDateValidator.class)
public @interface ValidBookingDates {
    String message() default "Invalid booking dates";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}