package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(Integer bookerId, CreateBookingRequest request);

    BookingResponse approveBooking(Integer ownerId, Integer bookingId, Boolean isApproved);

    BookingResponse getBooking(Integer userId, Integer bookingId);

    List<BookingResponse> getUserBookings(Integer userId, BookingState state);

    List<BookingResponse> getOwnerBookings(Integer ownerId, BookingState state);
}