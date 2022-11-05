package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepositoryOwnerCustom {
    Optional<Booking> findByIdAndItem_Owner(long bookingId, long userId);

    @Query(value = "SELECT * FROM bookings AS b JOIN items i on i.id = b.item_id " +
            "WHERE i.owner_id = ?1 AND (?2 between start_date and end_date)", nativeQuery = true)
    List<Booking> findAllByItem_OwnerAndStatusCurrent(long ownerId, LocalDateTime localDateTime);

    List<Booking> findAllByItem_OwnerOrderByStartDesc(long userid);

    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByIdDesc(long userid, LocalDateTime end);

    @Query(value = "SELECT * FROM bookings b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE status like ?2 AND i.owner_id = ?1", nativeQuery = true)
    List<Booking> findAllByOwner_IdAndByStatus(long ownerId, String state);

    @Query(value = "SELECT * from bookings " +
            "join items i on i.id = bookings.item_id " +
            "where owner_id = ? AND start_date > ? " +
            "ORDER BY bookings.id DESC",
            nativeQuery = true)
    List<Booking> findBookingsByOwnerAndStatusFuture(long ownerId, LocalDateTime dateTime);

    List<Booking> findByItem_IdAndItem_Owner(long itemId, long userId);
}
