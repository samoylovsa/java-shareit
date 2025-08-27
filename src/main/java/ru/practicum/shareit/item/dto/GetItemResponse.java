package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetItemResponse {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfoResponse lastBooking;
    private BookingInfoResponse nextBooking;
    private List<CommentResponse> comments;
}