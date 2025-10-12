package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createBooking_ShouldPersistBookingInDatabase() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        CreateBookingRequest request = CreateBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingResponse response = bookingService.createBooking(booker.getId(), request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(response.getItem().getId()).isEqualTo(item.getId());
        assertThat(response.getBooker().getId()).isEqualTo(booker.getId());

        Booking savedBooking = bookingRepository.findById(response.getId()).orElseThrow();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(savedBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(savedBooking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void approveBooking_ShouldApproveBooking_WhenValidRequest() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build());

        BookingResponse response = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertThat(response.getStatus()).isEqualTo(BookingStatus.APPROVED);

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenUserIsBooker() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build());

        BookingResponse response = bookingService.getBooking(booker.getId(), booking.getId());

        assertThat(response.getId()).isEqualTo(booking.getId());
        assertThat(response.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(response.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenUserIsOwner() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build());

        BookingResponse response = bookingService.getBooking(owner.getId(), booking.getId());

        assertThat(response.getId()).isEqualTo(booking.getId());
        assertThat(response.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(response.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void getUserBookings_ShouldReturnUserBookings() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build());

        List<BookingResponse> responses = bookingService.getUserBookings(booker.getId(), BookingState.ALL);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(booking.getId());
        assertThat(responses.get(0).getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void getOwnerBookings_ShouldReturnOwnerBookings() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build());

        List<BookingResponse> responses = bookingService.getOwnerBookings(owner.getId(), BookingState.ALL);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(booking.getId());
        assertThat(responses.get(0).getItem().getId()).isEqualTo(item.getId());
    }
}
