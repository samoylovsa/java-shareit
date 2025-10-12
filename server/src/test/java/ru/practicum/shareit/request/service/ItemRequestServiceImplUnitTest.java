package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private CreateItemRequestDto createItemRequestDto;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1).name("User").email("user@test.com").build();
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("Need a drill")
                .requester(user)
                .build();
        createItemRequestDto = CreateItemRequestDto.builder()
                .description("Need a drill")
                .build();
        item = Item.builder()
                .id(1)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
    }

    @Test
    void createItemRequest_ShouldReturnItemRequestResponse_WhenValidRequest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestResponse response = itemRequestService.createItemRequest(createItemRequestDto, 1);

        assertThat(response).isNotNull();
        verify(userRepository).findById(1);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequest_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.createItemRequest(createItemRequestDto, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with ID: 1");

        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getUserItemRequests_ShouldReturnListWithAnswers_WhenUserHasRequests() {
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1)).thenReturn(itemRequests);
        when(itemRepository.findByRequestIdIn(List.of(1))).thenReturn(List.of(item));

        List<ItemRequestWithAnswersResponse> responses = itemRequestService.getUserItemRequests(1);

        assertThat(responses).hasSize(1);
        verify(userRepository).findById(1);
        verify(itemRequestRepository).findByRequesterIdOrderByCreatedDesc(1);
        verify(itemRepository).findByRequestIdIn(List.of(1));
    }

    @Test
    void getUserItemRequests_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getUserItemRequests(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with ID: 1");

        verify(itemRequestRepository, never()).findByRequesterIdOrderByCreatedDesc(anyInt());
    }

    @Test
    void getAllItemRequests_ShouldReturnOtherUsersRequests_WhenValidUser() {
        User otherUser = User.builder().id(2).build();
        ItemRequest otherRequest = ItemRequest.builder().id(2).requester(otherUser).build();
        List<ItemRequest> requests = List.of(otherRequest);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(1)).thenReturn(requests);

        List<ItemRequestResponse> responses = itemRequestService.getAllItemRequests(1);

        assertThat(responses).hasSize(1);
        verify(userRepository).findById(1);
        verify(itemRequestRepository).findByRequesterIdNotOrderByCreatedDesc(1);
    }

    @Test
    void getAllItemRequests_ShouldReturnEmptyList_WhenNoOtherRequests() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(1)).thenReturn(Collections.emptyList());

        List<ItemRequestResponse> responses = itemRequestService.getAllItemRequests(1);

        assertThat(responses).isEmpty();
        verify(userRepository).findById(1);
        verify(itemRequestRepository).findByRequesterIdNotOrderByCreatedDesc(1);
    }

    @Test
    void getAllItemRequests_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getAllItemRequests(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with ID: 1");

        verify(itemRequestRepository, never()).findByRequesterIdNotOrderByCreatedDesc(anyInt());
    }

    @Test
    void getItemRequestById_ShouldReturnRequestWithAnswers_WhenValidIds() {
        List<Item> items = List.of(item);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(1)).thenReturn(items);

        ItemRequestWithAnswersResponse response = itemRequestService.getItemRequestById(1, 1);

        assertThat(response).isNotNull();
        verify(userRepository).findById(1);
        verify(itemRequestRepository).findById(1);
        verify(itemRepository).findByRequestId(1);
    }

    @Test
    void getItemRequestById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequestById(1, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with ID: 1");

        verify(itemRequestRepository, never()).findById(anyInt());
    }

    @Test
    void getItemRequestById_ShouldThrowException_WhenRequestNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequestById(1, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Request not found with ID: 1");

        verify(itemRepository, never()).findByRequestId(anyInt());
    }

    @Test
    void getItemRequestById_ShouldReturnRequestWithEmptyItems_WhenNoItemsFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(1)).thenReturn(Collections.emptyList());

        ItemRequestWithAnswersResponse response = itemRequestService.getItemRequestById(1, 1);

        assertThat(response).isNotNull();
        verify(userRepository).findById(1);
        verify(itemRequestRepository).findById(1);
        verify(itemRepository).findByRequestId(1);
    }
}