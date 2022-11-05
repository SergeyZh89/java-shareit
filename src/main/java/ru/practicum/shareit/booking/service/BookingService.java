package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(long userId, BookingDto bookingDto);

    BookingDto findByBookingIdAndUserId(long bookingId, long userId);

    BookingDto setApprove(long bookingId, long userId, boolean isApproved);

    List<BookingDto> findBookingsByUserByState(long userId, String state);

    List<BookingDto> findAllBookingsByOwner(long ownerId, String state);
}