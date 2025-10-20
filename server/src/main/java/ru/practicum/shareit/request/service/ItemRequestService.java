package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponse createItemRequest(CreateItemRequestDto createItemRequestDto, Integer userId);

    List<ItemRequestWithAnswersResponse> getUserItemRequests(Integer userId);

    List<ItemRequestResponse> getAllItemRequests(Integer userId);

    ItemRequestWithAnswersResponse getItemRequestById(Integer requestId, Integer userId);
}
