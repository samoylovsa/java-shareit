package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemResponse createItem(Integer ownerId, CreateItemRequest request) {
        User owner = findUser(ownerId);
        Item item = ItemMapper.mapToItem(owner, request);
        if (request.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(request.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item.setRequest(itemRequest);
        }
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
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentMapper::mapToCommentResponse)
                .toList();
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Booking lastBooking = bookingRepository.findLastBookingForItemWithBooker(itemId, now);
            Booking nextBooking = bookingRepository.findNextBookingForItemWithBooker(itemId, now);

            return ItemMapper.mapToGetItemResponse(item, lastBooking, nextBooking, commentResponses);
        } else {
            return ItemMapper.mapToGetItemResponse(item, null, null, commentResponses);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetItemResponse> getAllItemsByOwner(Integer ownerId) {
        User owner = findUser(ownerId);
        List<Item> items = itemRepository.findByOwnerId(owner.getId());

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Integer, List<CommentResponse>> commentsByItem = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::mapToCommentResponse, Collectors.toList())
                ));

        LocalDateTime now = LocalDateTime.now();
        Map<Integer, Booking> lastBookingsMap = bookingRepository.findLastBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), Function.identity()));
        Map<Integer, Booking> nextBookingsMap = bookingRepository.findNextBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), Function.identity()));

        return items.stream()
                .map(item -> {
                    List<CommentResponse> itemComments = commentsByItem.getOrDefault(item.getId(), Collections.emptyList());
                    Booking lastBooking = lastBookingsMap.get(item.getId());
                    Booking nextBooking = nextBookingsMap.get(item.getId());

                    return ItemMapper.mapToGetItemResponse(
                            item,
                            lastBooking,
                            nextBooking,
                            itemComments
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponse> searchItems(String text) {
        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::mapToItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse addComment(Integer itemId, Integer userId, CreateCommentRequest request) {
        User author = findUser(userId);
        Item item = findItem(itemId);

        validateUserRentedItem(userId, itemId);

        Comment comment = CommentMapper.mapToComment(request, item, author);
        comment = commentRepository.save(comment);

        return CommentMapper.mapToCommentResponse(comment);
    }

    private void validateUserRentedItem(Integer userId, Integer itemId) {
        LocalDateTime now = LocalDateTime.now();
        boolean hasRented = commentRepository.existsApprovedBookingForUserAndItem(itemId, userId, now, BookingStatus.APPROVED);

        if (!hasRented) {
            throw new IllegalArgumentException("User must have rented the item to leave a comment");
        }
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
