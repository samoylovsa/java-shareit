package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findWithItemAndOwnerById(@Param("bookingId") Integer bookingId);
}
