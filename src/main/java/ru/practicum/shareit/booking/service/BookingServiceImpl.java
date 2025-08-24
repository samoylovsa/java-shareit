package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(Integer bookerId, CreateBookingRequest request) {
        User booker = validateUserExists(bookerId);
        Item item = validateItemExists(request.getItemId());

        validateBookerIsNotOwner(booker.getId(), item.getOwner().getId());
        validateItemIsAvailable(item);

        Booking booking = BookingMapper.mapToBooking(request, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse approveBooking(Integer ownerId, Integer bookingId, Boolean isApproved) {
        User user = validateUserExists(ownerId);
        Booking booking = validateBookingExists(bookingId);

        validateUserIsOwner(user, booking);
        validateBookingStatusIsWaiting(booking);

        processApprove(booking, isApproved);
        booking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingResponse(booking);
    }

    private User validateUserExists(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("User with id %d not found", userId)
                ));
    }

    private Item validateItemExists(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Item with id %d not found", itemId)
                ));
    }

    private void validateBookerIsNotOwner(Integer bookerId, Integer ownerId) {
        if (bookerId.equals(ownerId)) {
            throw new IllegalArgumentException(
                    "Booker cannot be the owner of the item"
            );
        }
    }

    private void validateItemIsAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new IllegalArgumentException(
                    String.format("Item '%s' is not available for booking", item.getName())
            );
        }
    }

    private Booking validateBookingExists(Integer bookingId) {
        return bookingRepository.findWithItemAndOwnerById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Booking with id %d not found", bookingId)
                ));
    }

    private void validateUserIsOwner(User user, Booking booking) {
        Integer userId = user.getId();
        Integer ownerId = booking.getItem().getOwner().getId();
        if (!userId.equals(ownerId)) {
            throw new IllegalArgumentException(
                    "User must be the owner of the item"
            );
        }
    }

    private void validateBookingStatusIsWaiting(Booking booking) {
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalArgumentException(
                    "Booking status must be WAITING for owner approve"
            );
        }
    }

    private Booking processApprove(Booking booking, Boolean isApproved) {
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
            return booking;
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            return booking;
        }
    }
}
