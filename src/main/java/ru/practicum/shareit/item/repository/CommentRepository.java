package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByItemId(Integer itemId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.item LEFT JOIN FETCH c.author WHERE c.item.id IN :itemIds")
    List<Comment> findByItemIdIn(@Param("itemIds") List<Integer> itemIds);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.status = :status " +
            "AND b.end < :now")
    boolean existsApprovedBookingForUserAndItem(@Param("itemId") Integer itemId,
                                                @Param("userId") Integer userId,
                                                @Param("now") LocalDateTime now,
                                                @Param("status")BookingStatus status);
}
