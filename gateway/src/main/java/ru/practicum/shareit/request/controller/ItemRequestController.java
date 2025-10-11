package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @Valid @RequestBody CreateItemRequestDto createItemRequestDto,
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Received create item request request: {} by user ID: {}", createItemRequestDto, userId);
        ResponseEntity<Object> itemRequestResponse = itemRequestClient.createItemRequest(createItemRequestDto, userId);
        log.debug("Returning create item request response: {}", itemRequestResponse);
        return itemRequestResponse;
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Received get user item requests request by user ID: {}", userId);
        ResponseEntity<Object> responses = itemRequestClient.getUserItemRequests(userId);
        log.debug("Returning user item requests for user ID: {}", userId);
        return responses;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Received get all item requests request by user ID: {}", userId);
        ResponseEntity<Object> responses = itemRequestClient.getAllItemRequests(userId);
        log.debug("Returning item requests from other users for user ID: {}", userId);
        return responses;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @NotNull @Positive @PathVariable Integer requestId,
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Received get item request by ID request: request ID: {}, user ID: {}", requestId, userId);
        ResponseEntity<Object> response = itemRequestClient.getItemRequestById(requestId, userId);
        log.debug("Returning item request response for request ID: {}: {}", requestId, response);
        return response;
    }
}
