package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemResponse createItem(Integer ownerId, CreateItemRequest request) {
        validateUserExists(ownerId);
        Item item = ItemMapper.mapToItem(ownerId, request);
        item = itemRepository.createItem(item);
        return ItemMapper.mapToItemResponse(item);
    }

    @Override
    public ItemResponse updateItem(Integer ownerId, Integer itemId, UpdateItemRequest request) {
        validateUserExists(ownerId);
        Item existingItem = findItem(itemId);
        validateUserIsOwner(ownerId, existingItem.getOwnerId());
        Item updatedItem = updateRequiredItemFields(existingItem, request);
        return ItemMapper.mapToItemResponse(updatedItem);
    }

    @Override
    public ItemResponse getItemById(Integer itemId) {
        Item item = findItem(itemId);
        return ItemMapper.mapToItemResponse(item);
    }

    @Override
    public List<ItemResponse> getAllItemsByOwner(Integer ownerId) {
        validateUserExists(ownerId);
        return itemRepository.findItemsByOwner(ownerId)
                .stream()
                .map(ItemMapper::mapToItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponse> searchItems(String text) {
        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::mapToItemResponse)
                .collect(Collectors.toList());
    }

    private void validateUserExists(Integer ownerId) {
        userRepository.findUser(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + ownerId));
    }

    private Item findItem(Integer itemId) {
        return itemRepository.findItem(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + itemId));
    }

    private void validateUserIsOwner(Integer userId, Integer ownerId) {
        if (!userId.equals(ownerId)) {
            throw new NoAccessException("User is not the owner of item");
        }
    }

    private Item updateRequiredItemFields(Item existingItem, UpdateItemRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            existingItem.setName(request.getName());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            existingItem.setDescription(request.getDescription());
        }
        if (request.getAvailable() != null) {
            existingItem.setAvailable(request.getAvailable());
        }

        return itemRepository.updateItem(existingItem);
    }
}
