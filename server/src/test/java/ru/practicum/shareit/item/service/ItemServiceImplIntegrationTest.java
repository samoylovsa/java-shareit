package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void createItem_ShouldPersistItemInDatabase() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        CreateItemRequest request = CreateItemRequest.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        ItemResponse response = itemService.createItem(owner.getId(), request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Drill");
        assertThat(response.getDescription()).isEqualTo("Powerful drill");
        assertThat(response.getAvailable()).isTrue();

        Item savedItem = itemRepository.findById(response.getId()).orElseThrow();
        assertThat(savedItem.getName()).isEqualTo("Drill");
        assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void updateItem_ShouldUpdateItemInDatabase() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        Item existingItem = itemRepository.save(Item.builder()
                .name("Old Name")
                .description("Old Description")
                .available(true)
                .owner(owner)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .name("New Name")
                .description("New Description")
                .available(false)
                .build();

        ItemResponse response = itemService.updateItem(owner.getId(), existingItem.getId(), request);

        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getDescription()).isEqualTo("New Description");
        assertThat(response.getAvailable()).isFalse();

        Item updatedItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("New Name");
        assertThat(updatedItem.getDescription()).isEqualTo("New Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void getItemById_ShouldReturnBasicResponse_WhenUserIsNotOwner() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User otherUser = userRepository.save(User.builder()
                .name("Other User")
                .email("other@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build());

        GetItemResponse response = itemService.getItemById(item.getId(), otherUser.getId());

        assertThat(response.getId()).isEqualTo(item.getId());
        assertThat(response.getName()).isEqualTo("Item");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getAvailable()).isTrue();
    }

    @Test
    void getAllItemsByOwner_ShouldReturnOwnerItems() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        Item item1 = itemRepository.save(Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(owner)
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .owner(owner)
                .build());

        List<GetItemResponse> responses = itemService.getAllItemsByOwner(owner.getId());

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(GetItemResponse::getId)
                .containsExactlyInAnyOrder(item1.getId(), item2.getId());
    }

    @Test
    void getAllItemsByOwner_ShouldReturnEmptyList_WhenNoItems() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        List<GetItemResponse> responses = itemService.getAllItemsByOwner(owner.getId());

        assertThat(responses).isEmpty();
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        itemRepository.save(Item.builder()
                .name("Power Drill")
                .description("Very powerful drill")
                .available(true)
                .owner(owner)
                .build());

        itemRepository.save(Item.builder()
                .name("Hammer")
                .description("Steel hammer")
                .available(true)
                .owner(owner)
                .build());

        List<ItemResponse> results = itemService.searchItems("drill");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Power Drill");
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenNoMatches() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        List<ItemResponse> results = itemService.searchItems("nonexistent");

        assertThat(results).isEmpty();
    }

    @Test
    void addComment_ShouldPersistCommentInDatabase() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(booker)
                .build());

        CreateCommentRequest request = CreateCommentRequest.builder()
                .text("Great item!")
                .build();

        CommentResponse response = itemService.addComment(item.getId(), booker.getId(), request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getText()).isEqualTo("Great item!");
        assertThat(response.getAuthorName()).isEqualTo("Booker");

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getText()).isEqualTo("Great item!");
    }
}
