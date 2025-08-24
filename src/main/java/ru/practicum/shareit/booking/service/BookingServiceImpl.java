package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.CreateBookingResponse;
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
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public CreateBookingResponse createBooking(Integer bookerId, CreateBookingRequest request) {
        User booker = validateBookerExists(bookerId);
        Item item = validateItemExists(request.getItemId());
        validateBookerIsNotOwner(booker.getId(), item.getOwner().getId());
        validateItemIsAvailable(item);

        Booking booking = BookingMapper.mapToBooking(request, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        return BookingMapper.mapToCreateBookingResponse(booking);
    }

    private User validateBookerExists(Integer bookerId) {
        return userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d not found", bookerId)
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
}
