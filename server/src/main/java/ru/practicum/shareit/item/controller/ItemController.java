package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemResponse createItem(
            @RequestHeader(USER_ID_HEADER) Integer ownerId,
            @RequestBody CreateItemRequest request) {
        log.info("Received create item request: {} by owner ID: {}", request, ownerId);
        ItemResponse response = itemService.createItem(ownerId, request);
        log.debug("Returning create item response: {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(
            @RequestHeader(USER_ID_HEADER) Integer ownerId,
            @PathVariable Integer itemId,
            @RequestBody UpdateItemRequest request) {
        log.info("Received update item ID: {} request: {} by owner ID: {}", itemId, request, ownerId);
        ItemResponse response = itemService.updateItem(ownerId, itemId, request);
        log.debug("Returning update item response: {}", response);
        return response;
    }

    @GetMapping("/{itemId}")
    public GetItemResponse getItemById(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @PathVariable Integer itemId) {
        log.info("Received get request for item ID: {} by user ID: {}", itemId, userId);
        GetItemResponse response = itemService.getItemById(itemId, userId);
        log.debug("Returning item data: {}", response);
        return response;
    }

    @GetMapping
    public List<GetItemResponse> getAllItemsByOwner(
            @RequestHeader(USER_ID_HEADER) Integer ownerId) {
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
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @PathVariable Integer itemId,
            @RequestBody CreateCommentRequest request) {
        log.info("Received add comment request for item ID: {} by user ID: {}", itemId, userId);
        CommentResponse response = itemService.addComment(itemId, userId, request);
        log.debug("Returning comment response: {}", response);
        return response;
    }
}
