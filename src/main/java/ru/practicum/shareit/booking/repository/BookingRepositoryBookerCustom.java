package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepositoryBookerCustom {
    List<Booking> findByBooker_Id(long bookingId);

    @Query(value = "SELECT * FROM bookings AS b JOIN items i on i.id = b.item_id " +
            "WHERE b.booker_id= ?1 AND (?2 between start_date and end_date)", nativeQuery = true)
    List<Booking> findAllByBooker_IdAndStatusCurrent(long ownerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBooker_IdOrderByStartDesc(long userid, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByIdDesc(long userid, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByIdDesc(long userid, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT * FROM bookings b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE status like ?2 AND booker_id = ?1", nativeQuery = true)
    List<Booking> findAllByBooker_IdAndByStatus(long userid, String state, Pageable pageable);
}