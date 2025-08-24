package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.CreateBookingResponse;

public interface BookingService {

    CreateBookingResponse createBooking(Integer bookerId, CreateBookingRequest request);
}
