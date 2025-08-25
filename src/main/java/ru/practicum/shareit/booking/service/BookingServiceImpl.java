package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        Booking booking = validateBookingExists(bookingId);

        validateUserIsOwner(ownerId, booking);
        validateBookingStatusIsWaiting(booking);

        processApprove(booking, isApproved);
        booking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingResponse(booking);
    }

    @Override
    public BookingResponse getBooking(Integer userId, Integer bookingId) {
        User user = validateUserExists(userId);
        Booking booking = validateBookingExists(bookingId);

        validateUserHasAccess(user, booking);

        return BookingMapper.mapToBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getUserBookings(Integer userId, BookingState state) {
        User user = validateUserExists(userId);
        List<Booking> bookings = getBookingsByState(user.getId(), state);

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());

        return bookings.stream()
                .map(BookingMapper::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getOwnerBookings(Integer ownerId, BookingState state) {
        User owner = validateUserExists(ownerId);
        validateUserHasItems(ownerId);

        List<Booking> bookings = getOwnerBookingsByState(owner.getId(), state);

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());

        return bookings.stream()
                .map(BookingMapper::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    private User validateUserExists(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
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

    private void validateUserIsOwner(Integer userId, Booking booking) {
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

    private void processApprove(Booking booking, Boolean isApproved) {
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }

    private void validateUserHasAccess(User user, Booking booking) {
        Integer userId = user.getId();
        Integer bookerId = booking.getBooker().getId();
        Integer ownerId = booking.getItem().getOwner().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new WrongUserAccessException(
                    "User must be either booker or owner to access booking details"
            );
        }
    }

    private void validateUserHasItems(Integer ownerId) {
        long itemCount = itemRepository.countByOwnerId(ownerId);
        if (itemCount == 0) {
            throw new IllegalArgumentException("User must own at least one item to view owner bookings");
        }
    }

    private List<Booking> getBookingsByState(Integer userId, BookingState state) {
        switch (state) {
            case CURRENT:
                return bookingRepository.findCurrentByBookerIdWithRelations(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findPastByBookerIdWithRelations(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findFutureByBookerIdWithRelations(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusWithRelations(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusWithRelations(userId, BookingStatus.REJECTED);
            default:
                return bookingRepository.findByBookerIdWithRelations(userId);
        }
    }

    private List<Booking> getOwnerBookingsByState(Integer ownerId, BookingState state) {
        switch (state) {
            case CURRENT:
                return bookingRepository.findCurrentByItemOwnerIdWithRelations(ownerId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findPastByItemOwnerIdWithRelations(ownerId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findFutureByItemOwnerIdWithRelations(ownerId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItemOwnerIdAndStatusWithRelations(ownerId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByItemOwnerIdAndStatusWithRelations(ownerId, BookingStatus.REJECTED);
            default:
                return bookingRepository.findByItemOwnerIdWithRelations(ownerId);
        }
    }
}
