package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
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
    public BookingResponse createBooking(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer bookerId,
            @Valid @RequestBody CreateBookingRequest request) {
        log.info("Received create booking request: {} by booker ID: {}", request, bookerId);
        BookingResponse response = bookingService.createBooking(bookerId, request);
        log.debug("Returning create booking response: {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approveBooking(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer ownerId,
            @NotNull @Positive @PathVariable Integer bookingId,
            @NotNull @RequestParam Boolean approved) {
        log.info("Received approve booking request by owner ID: {} for booking ID: {}", ownerId, bookingId);
        BookingResponse response = bookingService.approveBooking(ownerId, bookingId, approved);
        log.debug("Returning approve booking response: {}", response);
        return response;
    }
}
