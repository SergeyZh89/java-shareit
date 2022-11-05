package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT * FROM bookings AS b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE b.id = ?1 AND (booker_id = ?2 OR owner_id = ?2)", nativeQuery = true)
    Optional<Booking> findByIdAndUserIdAndOwnerId(long bookingId, long userId);

    Optional<Booking> findByIdAndItem_Owner(long bookingId, long userId);

    List<Booking> findByBooker_Id(long bookingId);

    @Query(value = "SELECT * FROM bookings AS b JOIN items i on i.id = b.item_id " +
            "WHERE i.owner_id = ?1 AND (?2 between start_date and end_date)", nativeQuery = true)
    List<Booking> findAllByItem_OwnerAndStatusCurrent(long ownerId, LocalDateTime localDateTime);

    @Query(value = "SELECT * FROM bookings AS b JOIN items i on i.id = b.item_id " +
            "WHERE b.booker_id= ?1 AND (?2 between start_date and end_date)", nativeQuery = true)
    List<Booking> findAllByBooker_IdAndStatusCurrent(long ownerId, LocalDateTime localDateTime);

    List<Booking> findAllByBooker_IdOrderByStartDesc(long userid);

    List<Booking> findAllByItem_OwnerOrderByStartDesc(long userid);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByIdDesc(long userid, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByIdDesc(long userid, LocalDateTime end);
    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByIdDesc(long userid, LocalDateTime end);

    @Query(value = "SELECT * FROM bookings b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE status like ?2 AND booker_id = ?1", nativeQuery = true)
    List<Booking> findAllByBooker_IdAndByStatus(long userid, String state);

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

    List<Booking> findByItem_Id(long itemId);
}