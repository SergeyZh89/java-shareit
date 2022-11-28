package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_REQUEST_HEAD = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addNewBooking(@RequestHeader(USER_REQUEST_HEAD) long userId,
                                    @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос на добавление брони от пользователя: " + userId);
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findByBookingIdAndUserId(@PathVariable long bookingId,
                                               @RequestHeader(USER_REQUEST_HEAD) long userId) {
        log.info("Получен запрос на поиск брони по номеру: " + bookingId + " пользователя по номеру: " + userId);
        return bookingService.findByBookingIdAndUserId(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByOwner(@RequestHeader(USER_REQUEST_HEAD) long ownerId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false, defaultValue = "ALL")
                                                   String state) {
        log.info("Получен запрос на поиск брони по бронирующему: " + ownerId + " в статусе: " + state);
        int page = from / size;
        return bookingService.findAllBookingsByOwner(ownerId, state, PageRequest.of(page, size));
    }

    @GetMapping
    public List<BookingDto> findBookingsByUserIdByState(@RequestHeader(USER_REQUEST_HEAD) long userId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false, defaultValue = "ALL")
                                                        String state) {
        log.info("Получен запрос на поиск брони по владельцу: " + userId + " в статусе: " + state);
        int page = from / size;
        return bookingService.findBookingsByUserByState(userId, state, PageRequest.of(page, size));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@PathVariable long bookingId,
                                 @RequestParam boolean approved,
                                 @RequestHeader(USER_REQUEST_HEAD) long userId) {
        log.info("Получен запрос на изменение брони по номеру: " + bookingId
                + " от пользователя :" + userId
                + " на статус: " + approved);
        return bookingService.setApprove(bookingId, userId, approved);
    }
}