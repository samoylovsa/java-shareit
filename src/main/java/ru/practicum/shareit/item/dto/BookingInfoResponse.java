package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingInfoResponse {
    private LocalDateTime start;
    private LocalDateTime end;
}
