package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private CreateItemRequest createItemRequest;
    private UpdateItemRequest updateItemRequest;
    private CreateCommentRequest createCommentRequest;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1).name("Owner").email("owner@test.com").build();
        item = Item.builder()
                .id(1)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        createItemRequest = CreateItemRequest.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        updateItemRequest = UpdateItemRequest.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        createCommentRequest = CreateCommentRequest.builder()
                .text("Great item!")
                .build();
    }

    @Test
    void createItem_ShouldReturnItemResponse_WhenValidRequest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponse response = itemService.createItem(1, createItemRequest);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("Item");
        verify(userRepository).findById(1);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(1, createItemRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with ID: 1");

        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItem_ShouldSetItemRequest_WhenRequestIdProvided() {
        Integer requestId = 10;
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).build();
        createItemRequest.setRequestId(requestId);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemService.createItem(1, createItemRequest);

        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).save(argThat(savedItem ->
                savedItem.getRequest() != null && savedItem.getRequest().getId().equals(requestId)
        ));
    }

    @Test
    void createItem_ShouldThrowException_WhenItemRequestNotFound() {
        Integer requestId = 10;
        createItemRequest.setRequestId(requestId);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(1, createItemRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Запрос не найден");

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_WhenValidRequest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponse response = itemService.updateItem(1, 1, updateItemRequest);

        assertThat(response.getId()).isEqualTo(1);
        verify(userRepository).findById(1);
        verify(itemRepository).findById(1);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowException_WhenUserNotOwner() {
        User otherUser = User.builder().id(2).build();
        when(userRepository.findById(2)).thenReturn(Optional.of(otherUser));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.updateItem(2, 1, updateItemRequest))
                .isInstanceOf(NoAccessException.class)
                .hasMessage("User is not the owner of item");

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_ShouldUpdateOnlyName_WhenOnlyNameProvided() {
        UpdateItemRequest partialRequest = UpdateItemRequest.builder()
                .name("Only Name Updated")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemService.updateItem(1, 1, partialRequest);

        verify(itemRepository).save(argThat(savedItem ->
                savedItem.getName().equals("Only Name Updated")
        ));
    }

    @Test
    void getItemById_ShouldReturnFullResponse_WhenUserIsOwner() {
        Integer userId = 1;
        Integer itemId = 1;
        List<Comment> comments = Collections.emptyList();
        Booking lastBooking = Booking.builder().id(1).build();
        Booking nextBooking = Booking.builder().id(2).build();

        when(itemRepository.findByIdWithOwner(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(itemId)).thenReturn(comments);
        when(bookingRepository.findLastBookingForItemWithBooker(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findNextBookingForItemWithBooker(eq(itemId), any(LocalDateTime.class)))
                .thenReturn(nextBooking);

        GetItemResponse response = itemService.getItemById(itemId, userId);

        assertThat(response).isNotNull();
        verify(itemRepository).findByIdWithOwner(itemId);
        verify(commentRepository).findByItemId(itemId);
        verify(bookingRepository).findLastBookingForItemWithBooker(eq(itemId), any(LocalDateTime.class));
        verify(bookingRepository).findNextBookingForItemWithBooker(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    void getItemById_ShouldReturnBasicResponse_WhenUserIsNotOwner() {
        Integer userId = 2; // Different user
        Integer itemId = 1;
        List<Comment> comments = Collections.emptyList();

        when(itemRepository.findByIdWithOwner(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(itemId)).thenReturn(comments);

        GetItemResponse response = itemService.getItemById(itemId, userId);

        assertThat(response).isNotNull();
        verify(bookingRepository, never()).findLastBookingForItemWithBooker(any(), any());
        verify(bookingRepository, never()).findNextBookingForItemWithBooker(any(), any());
    }

    @Test
    void getItemById_ShouldThrowException_WhenItemNotFound() {
        Integer itemId = 999;
        when(itemRepository.findByIdWithOwner(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemById(itemId, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item not found");
    }

    @Test
    void getAllItemsByOwner_ShouldReturnEmptyList_WhenNoItems() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(Collections.emptyList());

        List<GetItemResponse> result = itemService.getAllItemsByOwner(1);

        assertThat(result).isEmpty();
        verify(userRepository).findById(1);
        verify(itemRepository).findByOwnerId(1);
    }

    @Test
    void searchItems_ShouldReturnItems_WhenTextProvided() {
        String searchText = "test";
        List<Item> items = List.of(item);
        when(itemRepository.searchAvailableItems(searchText)).thenReturn(items);

        List<ItemResponse> result = itemService.searchItems(searchText);

        assertThat(result).hasSize(1);
        verify(itemRepository).searchAvailableItems(searchText);
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenNoMatches() {
        String searchText = "nonexistent";
        when(itemRepository.searchAvailableItems(searchText)).thenReturn(Collections.emptyList());

        List<ItemResponse> result = itemService.searchItems(searchText);

        assertThat(result).isEmpty();
        verify(itemRepository).searchAvailableItems(searchText);
    }

    @Test
    void addComment_ShouldThrowException_WhenUserDidNotRentItem() {
        Integer itemId = 1;
        Integer userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.existsApprovedBookingForUserAndItem(eq(itemId), eq(userId), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.addComment(itemId, userId, createCommentRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User must have rented the item to leave a comment");

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_ShouldThrowException_WhenUserNotFound() {
        Integer itemId = 1;
        Integer userId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(itemId, userId, createCommentRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with ID: " + userId);

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_ShouldThrowException_WhenItemNotFound() {
        Integer itemId = 999;
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(itemId, userId, createCommentRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item not found with ID: " + itemId);

        verify(commentRepository, never()).save(any());
    }
}