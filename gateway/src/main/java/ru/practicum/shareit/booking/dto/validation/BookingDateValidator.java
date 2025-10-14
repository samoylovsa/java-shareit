package ru.practicum.shareit.booking.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.time.LocalDateTime;

public class BookingDateValidator
        implements ConstraintValidator<ValidBookingDates, CreateBookingRequest> {

    @Override
    public void initialize(ValidBookingDates constraintAnnotation) { }

    @Override
    public boolean isValid(CreateBookingRequest request, ConstraintValidatorContext context) {
        if (request.getStart() == null || request.getEnd() == null) {
            return true;
        }

        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();

        boolean isValid = true;

        if (end.isBefore(start)) {
            addConstraintViolation(context, "End date must be after start date", "end");
            isValid = false;
        }

        if (start.isEqual(end)) {
            addConstraintViolation(context, "Start and end dates cannot be equal", "start");
            isValid = false;
        }

        return isValid;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message, String fieldName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}