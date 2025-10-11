package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponse createBooking(
            @RequestHeader(USER_ID_HEADER) Integer bookerId,
            @RequestBody CreateBookingRequest request) {
        log.info("Received create booking request: {} by booker ID: {}", request, bookerId);
        BookingResponse response = bookingService.createBooking(bookerId, request);
        log.debug("Returning create booking response: {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approveBooking(
            @RequestHeader(USER_ID_HEADER) Integer ownerId,
            @PathVariable Integer bookingId,
            @RequestParam Boolean approved) {
        log.info("Received approve booking request by owner ID: {} for booking ID: {}", ownerId, bookingId);
        BookingResponse response = bookingService.approveBooking(ownerId, bookingId, approved);
        log.debug("Returning approve booking response: {}", response);
        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @PathVariable Integer bookingId) {
        log.info("Received get booking request by user ID: {} for booking ID: {}", userId, bookingId);
        BookingResponse response = bookingService.getBooking(userId, bookingId);
        log.debug("Returning booking response: {}", response);
        return response;
    }

    @GetMapping
    public List<BookingResponse> getUserBookings(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Received get user bookings request by user ID: {} with state: {}", userId, state);
        List<BookingResponse> response = bookingService.getUserBookings(userId, state);
        log.debug("Returning {} bookings for user ID: {}", response.size(), userId);
        return response;
    }

    @GetMapping("/owner")
    public List<BookingResponse> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) Integer ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Received get owner bookings request by owner ID: {} with state: {}", ownerId, state);
        List<BookingResponse> response = bookingService.getOwnerBookings(ownerId, state);
        log.debug("Returning {} bookings for owner ID: {}", response.size(), ownerId);
        return response;
    }
}
