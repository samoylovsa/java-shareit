package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.CreateBookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public CreateBookingResponse createBooking(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer bookerId,
            @Valid @RequestBody CreateBookingRequest request) {
        log.info("Received create booking request: {} by booker ID: {}", request, bookerId);
        CreateBookingResponse response = bookingService.createBooking(bookerId, request);
        log.debug("Returning create booking response: {}", response);
        return response;
    }
}
