package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collections;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long ownerId,
            @Valid @RequestBody CreateItemRequest request) {
        log.info("Received create item request: {} by owner ID: {}", request, ownerId);
        ResponseEntity<Object> response = itemClient.createItem(ownerId, request);
        log.debug("Returning create item response: {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long ownerId,
            @NotNull @Positive @PathVariable Long itemId,
            @RequestBody UpdateItemRequest request) {
        log.info("Received update item ID: {} request: {} by owner ID: {}", itemId, request, ownerId);
        ResponseEntity<Object> response = itemClient.updateItem(ownerId, itemId, request);
        log.debug("Returning update item response: {}", response);
        return response;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long userId,
            @NotNull @Positive @PathVariable Long itemId) {
        log.info("Received get request for item ID: {} by user ID: {}", itemId, userId);
        ResponseEntity<Object> response = itemClient.getItemById(itemId, userId);
        log.debug("Returning item data: {}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Received get all items request for owner ID: {}", ownerId);
        ResponseEntity<Object> response = itemClient.getAllItemsByOwner(ownerId);
        log.debug("Returning all items response: {}", response);
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItems(@RequestParam String text) {
        log.info("Received search request with text: {}", text);
        if (text == null || text.isBlank()) {
            log.debug("Search text is blank. Returning response with empty list");
            return ResponseEntity.ok(Collections.emptyList());
        }
        ResponseEntity<Object> response = itemClient.searchItems(text);
        log.debug("Returning search result response: {}", response);
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Long userId,
            @NotNull @Positive @PathVariable Long itemId,
            @Valid @RequestBody CreateCommentRequest request) {
        log.info("Received add comment request for item ID: {} by user ID: {}", itemId, userId);
        ResponseEntity<Object> response = itemClient.addComment(itemId, userId, request);
        log.debug("Returning comment response: {}", response);
        return response;
    }
}
