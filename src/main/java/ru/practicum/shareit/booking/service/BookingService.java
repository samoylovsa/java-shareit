package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(Integer bookerId, CreateBookingRequest request);

    BookingResponse approveBooking(Integer ownerId, Integer bookingId, Boolean isApproved);
}
