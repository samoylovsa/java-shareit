package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createItemRequest_ShouldPersistItemRequestInDatabase() {
        User user = userRepository.save(User.builder()
                .name("User")
                .email("user@example.com")
                .build());

        CreateItemRequestDto requestDto = CreateItemRequestDto.builder()
                .description("Need a power drill for home repairs")
                .build();

        ItemRequestResponse response = itemRequestService.createItemRequest(requestDto, user.getId());

        assertThat(response.getId()).isNotNull();
        assertThat(response.getDescription()).isEqualTo("Need a power drill for home repairs");
    }

    @Test
    void getUserItemRequests_ShouldReturnUserRequestsWithAnswers() {
        User user = userRepository.save(User.builder()
                .name("User")
                .email("user@example.com")
                .build());

        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Need a drill")
                .requester(user)
                .build());

        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build());

        List<ItemRequestWithAnswersResponse> responses = itemRequestService.getUserItemRequests(user.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(itemRequest.getId());
        assertThat(responses.get(0).getItems()).hasSize(1);
    }

    @Test
    void getAllItemRequests_ShouldReturnOtherUsersRequests() {
        User user1 = userRepository.save(User.builder()
                .name("User1")
                .email("user1@example.com")
                .build());

        User user2 = userRepository.save(User.builder()
                .name("User2")
                .email("user2@example.com")
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("Request from user2")
                .requester(user2)
                .build());

        List<ItemRequestResponse> responses = itemRequestService.getAllItemRequests(user1.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getDescription()).isEqualTo("Request from user2");
    }

    @Test
    void getItemRequestById_ShouldReturnRequestWithAnswers() {
        User user = userRepository.save(User.builder()
                .name("User")
                .email("user@example.com")
                .build());

        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Need a drill")
                .requester(user)
                .build());

        itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build());

        ItemRequestWithAnswersResponse response = itemRequestService.getItemRequestById(itemRequest.getId(), user.getId());

        assertThat(response.getId()).isEqualTo(itemRequest.getId());
        assertThat(response.getItems()).hasSize(1);
    }
}
