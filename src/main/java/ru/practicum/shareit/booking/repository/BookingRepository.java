package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        BookingRepositoryOwnerCustom,
        BookingRepositoryBookerCustom {

    @Query(value = "SELECT * FROM bookings AS b " +
            "JOIN items i on i.id = b.item_id " +
            "WHERE b.id = ?1 AND (booker_id = ?2 OR owner_id = ?2)", nativeQuery = true)
    Optional<Booking> findByIdAndUserIdAndOwnerId(long bookingId, long userId);

    List<Booking> findByItem_Id(long itemId);
}