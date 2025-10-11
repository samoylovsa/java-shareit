package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateBookingRequest {

    @NotNull(message = "Item ID cannot be empty")
    private Integer itemId;

    @NotNull(message = "Start date cannot be empty")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime start;

    @NotNull(message = "End date cannot be empty")
    @Future(message = "End date must be in the future")
    private LocalDateTime end;
}
