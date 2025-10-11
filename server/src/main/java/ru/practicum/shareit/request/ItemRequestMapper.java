package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemAnswer;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(CreateItemRequestDto createItemRequestDto, User user) {
        return ItemRequest.builder()
                .description(createItemRequestDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestResponse mapToItemRequestResponse(ItemRequest itemRequest) {
        return ItemRequestResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestWithAnswersResponse mapToItemRequestWithAnswersResponse(ItemRequest itemRequest, List<Item> items) {
        List<ItemAnswer> itemAnswers = items.stream()
                .map(item -> ItemAnswer.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .itemOwnerId(item.getOwner().getId())
                        .build())
                .collect(Collectors.toList());
        return ItemRequestWithAnswersResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemAnswers)
                .build();
    }
}
