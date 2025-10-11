package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static Item mapToItem(User owner, CreateItemRequest request) {
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .owner(owner)
                .build();
    }

    public static ItemResponse mapToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static GetItemResponse mapToGetItemResponse(Item item, Booking lastBooking,
                                                       Booking nextBooking, List<CommentResponse> comments) {
        GetItemResponse.GetItemResponseBuilder builder = GetItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments != null ? comments : Collections.emptyList());

        if (lastBooking != null) {
            builder.lastBooking(mapToBookingInfo(lastBooking));
        }
        if (nextBooking != null) {
            builder.nextBooking(mapToBookingInfo(nextBooking));
        }

        return builder.build();
    }

    private static BookingInfoResponse mapToBookingInfo(Booking booking) {
        return BookingInfoResponse.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}