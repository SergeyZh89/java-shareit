package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(long userId, BookingDto bookingDto);

    BookingDto findByBookingIdAndUserId(long bookingId, long userId);

    BookingDto setApprove(long bookingId, long userId, boolean isApproved);

    List<BookingDto> findBookingsByUserByState(long userId, String state, Pageable pageable);

    List<BookingDto> findAllBookingsByOwner(long ownerId, String state, Pageable pageable);
}