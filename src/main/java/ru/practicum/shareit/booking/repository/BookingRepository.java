package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findWithItemAndOwnerById(@Param("bookingId") Integer bookingId);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdWithRelations(@Param("bookerId") Integer bookerId);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerIdWithRelations(@Param("bookerId") Integer bookerId,
                                                     @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByBookerIdWithRelations(@Param("bookerId") Integer bookerId,
                                                  @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByBookerIdWithRelations(@Param("bookerId") Integer bookerId,
                                                    @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStatusWithRelations(@Param("bookerId") Integer bookerId,
                                                       @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdWithRelations(@Param("ownerId") Integer ownerId);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByItemOwnerIdWithRelations(@Param("ownerId") Integer ownerId,
                                                        @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByItemOwnerIdWithRelations(@Param("ownerId") Integer ownerId,
                                                     @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByItemOwnerIdWithRelations(@Param("ownerId") Integer ownerId,
                                                       @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH b.booker " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStatusWithRelations(@Param("ownerId") Integer ownerId,
                                                          @Param("status") BookingStatus status);
}
