package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Slf4j
@NoArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос на добавление брони от пользователя: " + userId);
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findByBookingIdAndUserId(@PathVariable long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на поиск брони по номеру: " + bookingId + " пользователя по номеру: " + userId);
        return bookingService.findByBookingIdAndUserId(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос на поиск брони по бронирующему: " + ownerId + " в статусе: " + state);
        return bookingService.findAllBookingsByOwner(ownerId, state);
    }

    @GetMapping
    public List<BookingDto> findBookingsByUserIdByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Получен запрос на поиск брони по владельцу: " + userId + " в статусе: " + state);
        return bookingService.findBookingsByUserByState(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@PathVariable long bookingId,
                                 @RequestParam boolean approved,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на изменение брони по номеру: " + bookingId
                + " от пользователя :" + userId
                + " на статус: " + approved);
        return bookingService.setApprove(bookingId, userId, approved);
    }
}