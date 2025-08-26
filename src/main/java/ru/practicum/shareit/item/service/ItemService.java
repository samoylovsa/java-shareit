package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.GetItemResponse;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemResponse createItem(Integer ownerId, CreateItemRequest request);

    ItemResponse updateItem(Integer ownerId, Integer itemId, UpdateItemRequest request);

    GetItemResponse getItemById(Integer itemId, Integer userId);

    List<GetItemResponse> getAllItemsByOwner(Integer ownerId);

    List<ItemResponse> searchItems(String text);
}
