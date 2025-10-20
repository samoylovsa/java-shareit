package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private CreateBookingRequest createBookingRequest;

    @BeforeEach
    void setUp() {
        booker = User.builder().id(1).name("Booker").email("booker@test.com").build();
        owner = User.builder().id(2).name("Owner").email("owner@test.com").build();
        item = Item.builder()
                .id(1)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();
        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();
        createBookingRequest = CreateBookingRequest.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void createBooking_ShouldReturnBookingResponse_WhenValidRequest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.createBooking(1, createBookingRequest);

        assertThat(response).isNotNull();
        verify(userRepository).findById(1);
        verify(itemRepository).findById(1);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(1, createBookingRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id 1 not found");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_ShouldThrowException_WhenItemNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(1, createBookingRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Item with id 1 not found");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_ShouldThrowException_WhenBookerIsOwner() {
        when(userRepository.findById(2)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(2, createBookingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Booker cannot be the owner of the item");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_ShouldThrowException_WhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(1, createBookingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item 'Item' is not available for booking");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBooking_ShouldReturnApprovedResponse_WhenValidApproval() {
        when(bookingRepository.findWithItemAndOwnerById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.approveBooking(2, 1, true);

        assertThat(response).isNotNull();
        verify(bookingRepository).findWithItemAndOwnerById(1);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void approveBooking_ShouldReturnRejectedResponse_WhenValidRejection() {
        when(bookingRepository.findWithItemAndOwnerById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.approveBooking(2, 1, false);

        assertThat(response).isNotNull();
        verify(bookingRepository).findWithItemAndOwnerById(1);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void approveBooking_ShouldThrowException_WhenBookingNotFound() {
        when(bookingRepository.findWithItemAndOwnerById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.approveBooking(2, 1, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Booking with id 1 not found");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBooking_ShouldThrowException_WhenUserNotOwner() {
        when(bookingRepository.findWithItemAndOwnerById(1)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approveBooking(999, 1, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User must be the owner of the item");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBooking_ShouldThrowException_WhenStatusNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findWithItemAndOwnerById(1)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approveBooking(2, 1, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Booking status must be WAITING for owner approve");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBooking_ShouldReturnBookingResponse_WhenUserIsBooker() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findWithItemAndOwnerById(1)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBooking(1, 1);

        assertThat(response).isNotNull();
        verify(userRepository).findById(1);
        verify(bookingRepository).findWithItemAndOwnerById(1);
    }

    @Test
    void getBooking_ShouldReturnBookingResponse_WhenUserIsOwner() {
        when(userRepository.findById(2)).thenReturn(Optional.of(owner));
        when(bookingRepository.findWithItemAndOwnerById(1)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBooking(2, 1);

        assertThat(response).isNotNull();
        verify(userRepository).findById(2);
        verify(bookingRepository).findWithItemAndOwnerById(1);
    }

    @Test
    void getBooking_ShouldThrowException_WhenUserNoAccess() {
        User otherUser = User.builder().id(999).build();
        when(userRepository.findById(999)).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findWithItemAndOwnerById(1)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBooking(999, 1))
                .isInstanceOf(WrongUserAccessException.class)
                .hasMessage("User must be either booker or owner to access booking details");

        verify(bookingRepository).findWithItemAndOwnerById(1);
    }

    @Test
    void getUserBookings_ShouldReturnBookings_WhenValidRequest() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdWithRelations(1)).thenReturn(new ArrayList<>(List.of(booking)));

        List<BookingResponse> responses = bookingService.getUserBookings(1, BookingState.ALL);

        assertThat(responses).hasSize(1);
        verify(userRepository).findById(1);
        verify(bookingRepository).findByBookerIdWithRelations(1);
    }

    @Test
    void getUserBookings_ShouldCallCorrectRepositoryMethod_WhenCurrentState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentByBookerIdWithRelations(eq(1), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>(List.of(booking)));

        bookingService.getUserBookings(1, BookingState.CURRENT);

        verify(bookingRepository).findCurrentByBookerIdWithRelations(eq(1), any(LocalDateTime.class));
    }

    @Test
    void getOwnerBookings_ShouldReturnBookings_WhenValidRequest() {
        when(userRepository.findById(2)).thenReturn(Optional.of(owner));
        when(itemRepository.countByOwnerId(2)).thenReturn(1L);
        when(bookingRepository.findByItemOwnerIdWithRelations(2)).thenReturn(new ArrayList<>(List.of(booking)));

        List<BookingResponse> responses = bookingService.getOwnerBookings(2, BookingState.ALL);

        assertThat(responses).hasSize(1);
        verify(userRepository).findById(2);
        verify(itemRepository).countByOwnerId(2);
        verify(bookingRepository).findByItemOwnerIdWithRelations(2);
    }

    @Test
    void getOwnerBookings_ShouldThrowException_WhenUserHasNoItems() {
        when(userRepository.findById(2)).thenReturn(Optional.of(owner));
        when(itemRepository.countByOwnerId(2)).thenReturn(0L);

        assertThatThrownBy(() -> bookingService.getOwnerBookings(2, BookingState.ALL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User must own at least one item to view owner bookings");

        verify(bookingRepository, never()).findByItemOwnerIdWithRelations(anyInt());
    }

    @Test
    void getOwnerBookings_ShouldCallCorrectRepositoryMethod_WhenFutureState() {
        when(userRepository.findById(2)).thenReturn(Optional.of(owner));
        when(itemRepository.countByOwnerId(2)).thenReturn(1L);
        when(bookingRepository.findFutureByItemOwnerIdWithRelations(eq(2), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>(List.of(booking)));

        bookingService.getOwnerBookings(2, BookingState.FUTURE);

        verify(bookingRepository).findFutureByItemOwnerIdWithRelations(eq(2), any(LocalDateTime.class));
    }

    @Test
    void getUserBookings_ShouldReturnEmptyList_WhenNoBookings() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdWithRelations(1)).thenReturn(Collections.emptyList());

        List<BookingResponse> responses = bookingService.getUserBookings(1, BookingState.ALL);

        assertThat(responses).isEmpty();
        verify(userRepository).findById(1);
        verify(bookingRepository).findByBookerIdWithRelations(1);
    }
}