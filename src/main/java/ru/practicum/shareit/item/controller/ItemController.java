package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemResponse createItem(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer ownerId,
            @Valid @RequestBody CreateItemRequest request) {
        log.info("Received create item request: {} by owner ID: {}", request, ownerId);
        ItemResponse response = itemService.createItem(ownerId, request);
        log.debug("Returning create item response: {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer ownerId,
            @NotNull @Positive @PathVariable Integer itemId,
            @RequestBody UpdateItemRequest request) {
        log.info("Received update item ID: {} request: {} by owner ID: {}", itemId, request, ownerId);
        ItemResponse response = itemService.updateItem(ownerId, itemId, request);
        log.debug("Returning update item response: {}", response);
        return response;
    }

    @GetMapping("/{itemId}")
    public GetItemResponse getItemById(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer userId,
            @NotNull @Positive @PathVariable Integer itemId) {
        log.info("Received get request for item ID: {} by user ID: {}", itemId, userId);
        GetItemResponse response = itemService.getItemById(itemId, userId);
        log.debug("Returning item data: {}", response);
        return response;
    }

    @GetMapping
    public List<GetItemResponse> getAllItemsByOwner(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer ownerId) {
        log.info("Received get all items request for owner ID: {}", ownerId);
        List<GetItemResponse> response = itemService.getAllItemsByOwner(ownerId);
        log.debug("Returning all items response: {}", response);
        return response;
    }

    @GetMapping("/search")
    public List<ItemResponse> searchAvailableItems(@RequestParam String text) {
        log.info("Received search request with text: {}", text);
        if (text == null || text.isBlank()) {
            log.debug("Search text is blank. Returning response with empty list");
            return List.of();
        }
        List<ItemResponse> response = itemService.searchItems(text);
        log.debug("Returning search result response: {}", response);
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(
            @NotNull @Positive @RequestHeader(USER_ID_HEADER) Integer userId,
            @NotNull @Positive @PathVariable Integer itemId,
            @Valid @RequestBody CreateCommentRequest request) {
        log.info("Received add comment request for item ID: {} by user ID: {}", itemId, userId);
        CommentResponse response = itemService.addComment(itemId, userId, request);
        log.debug("Returning comment response: {}", response);
        return response;
    }
}
