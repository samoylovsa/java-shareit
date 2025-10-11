package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponse createItemRequest(
            @RequestBody CreateItemRequestDto createItemRequestDto,
            @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Received create item request request: {} by user ID: {}", createItemRequestDto, userId);
        ItemRequestResponse itemRequestResponse = itemRequestService.createItemRequest(createItemRequestDto, userId);
        log.debug("Returning create item request response: {}", itemRequestResponse);
        return itemRequestResponse;
    }

    @GetMapping
    public List<ItemRequestWithAnswersResponse> getUserItemRequests(
            @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Received get user item requests request by user ID: {}", userId);
        List<ItemRequestWithAnswersResponse> responses = itemRequestService.getUserItemRequests(userId);
        log.debug("Returning {} user item requests for user ID: {}", responses.size(), userId);
        return responses;
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllItemRequests(
            @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Received get all item requests request by user ID: {}", userId);
        List<ItemRequestResponse> responses = itemRequestService.getAllItemRequests(userId);
        log.debug("Returning {} item requests from other users for user ID: {}", responses.size(), userId);
        return responses;
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersResponse getItemRequestById(
            @PathVariable Integer requestId,
            @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Received get item request by ID request: request ID: {}, user ID: {}", requestId, userId);
        ItemRequestWithAnswersResponse response = itemRequestService.getItemRequestById(requestId, userId);
        log.debug("Returning item request response for request ID: {}: {}", requestId, response);
        return response;
    }
}

