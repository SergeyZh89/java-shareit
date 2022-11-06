package ru.practicum.shareit.booking.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface BookingService {
    BookingDto addNewBooking(long userId, @Valid BookingDto bookingDto);

    BookingDto findByBookingIdAndUserId(long bookingId, long userId);

    BookingDto setApprove(long bookingId, long userId, boolean isApproved);

    List<BookingDto> findBookingsByUserByState(long userId, String state);

    List<BookingDto> findAllBookingsByOwner(long ownerId, String state);
}