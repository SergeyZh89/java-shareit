package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                new BookingDto.Item(
                        booking.getItem().getId(),
                        booking.getItem().getName()
                ),
                new BookingDto.User(
                        booking.getBooker().getId()
                ),
                booking.getStatus());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(0, 0, null, null, null),
                new User());
    }
}