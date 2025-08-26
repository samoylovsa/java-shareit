package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.GetItemResponse;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemResponse createItem(Integer ownerId, CreateItemRequest request) {
        User owner = findUser(ownerId);
        Item item = ItemMapper.mapToItem(owner, request);
        item = itemRepository.save(item);
        return ItemMapper.mapToItemResponse(item);
    }

    @Override
    @Transactional
    public ItemResponse updateItem(Integer ownerId, Integer itemId, UpdateItemRequest request) {
        User owner = findUser(ownerId);
        Item existingItem = findItem(itemId);
        validateUserIsOwner(owner.getId(), existingItem.getOwner().getId());
        Item updatedItem = updateRequiredItemFields(existingItem, request);
        return ItemMapper.mapToItemResponse(updatedItem);
    }

    @Override
    public GetItemResponse getItemById(Integer itemId, Integer userId) {
        Item item = findItemWithOwner(itemId);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Booking lastBooking = bookingRepository.findLastBookingForItemWithBooker(itemId, now);
            Booking nextBooking = bookingRepository.findNextBookingForItemWithBooker(itemId, now);

            return ItemMapper.mapToGetItemResponse(item, lastBooking, nextBooking);
        } else {
            return ItemMapper.mapToGetItemResponse(item, null, null);
        }
    }

    @Override
    public List<GetItemResponse> getAllItemsByOwner(Integer ownerId) {
        User owner = findUser(ownerId);
        List<Item> items = itemRepository.findByOwnerId(owner.getId());

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, Booking> lastBookingsMap;
        Map<Integer, Booking> nextBookingsMap;

        if (items.getFirst().getOwner().getId().equals(ownerId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Integer> itemIds = items.stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());

            lastBookingsMap = bookingRepository.findLastBookingsForItems(itemIds, now)
                    .stream()
                    .collect(Collectors.toMap(b -> b.getItem().getId(), Function.identity()));

            nextBookingsMap = bookingRepository.findNextBookingsForItems(itemIds, now)
                    .stream()
                    .collect(Collectors.toMap(b -> b.getItem().getId(), Function.identity()));
        } else {
            nextBookingsMap = Collections.emptyMap();
            lastBookingsMap = Collections.emptyMap();
        }

        return items.stream()
                .map(item -> ItemMapper.mapToGetItemResponse(
                        item,
                        lastBookingsMap.get(item.getId()),
                        nextBookingsMap.get(item.getId())
                )).collect(Collectors.toList());
    }

    @Override
    public List<ItemResponse> searchItems(String text) {
        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::mapToItemResponse)
                .collect(Collectors.toList());
    }

    private User findUser(Integer ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + ownerId));
    }

    private Item findItem(Integer itemId) {
        return itemRepository.findById(itemId)
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

        return itemRepository.save(existingItem);
    }

    private Item findItemWithOwner(Integer itemId) {
        return itemRepository.findByIdWithOwner(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }
}
