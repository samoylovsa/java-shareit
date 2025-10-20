package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long bookerId,
            @Valid @RequestBody CreateBookingRequest request) {
        log.info("Received create booking request: {} by booker ID: {}", request, bookerId);
        ResponseEntity<Object> response = bookingClient.createBooking(bookerId, request);
        log.debug("Returning create booking response: {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long ownerId,
            @NotNull @Positive @PathVariable Long bookingId,
            @NotNull @RequestParam Boolean approved) {
        log.info("Received approve booking request by owner ID: {} for booking ID: {}", ownerId, bookingId);
        ResponseEntity<Object> response = bookingClient.approveBooking(ownerId, bookingId, approved);
        log.debug("Returning approve booking response: {}", response);
        return response;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long userId,
            @NotNull @Positive @PathVariable Long bookingId) {
        log.info("Received get booking request by user ID: {} for booking ID: {}", userId, bookingId);
        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);
        log.debug("Returning booking response: {}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Received get user bookings request by user ID: {} with state: {}", userId, state);
        ResponseEntity<Object> response = bookingClient.getUserBookings(userId, state);
        log.debug("Returning bookings for user ID: {}", userId);
        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Received get owner bookings request by owner ID: {} with state: {}", ownerId, state);
        ResponseEntity<Object> response = bookingClient.getOwnerBookings(ownerId, state);
        log.debug("Returning bookings for owner ID: {}", ownerId);
        return response;
    }
}
