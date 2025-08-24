package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateBookingResponse {
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserResponse booker;
    private ItemResponse item;
}
