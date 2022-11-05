package ru.practicum.shareit.booking.service.Impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.state.State.*;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public BookingServiceImpl(@Lazy BookingRepository bookingRepository,
                              UserService userService,
                              ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto addNewBooking(long userId, BookingDto bookingDto) {
        if (!itemService.getItemById(bookingDto
                        .getItemId())
                .getAvailable()) {
            throw new ValidatorExceptions("Данная вещь недоступна");
        }
        if (userId == bookingDto.getItemId()) {
            throw new ItemNotFoundException("Владелец вещи не может бронировать свою вещь");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidatorExceptions("Время начала бронирования раньше времени окончания бронирования ");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidatorExceptions("Время начала бронирования в прошлом");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidatorExceptions("Время окончания бронирования в прошлом");
        }
        User user = UserMapper.toUser(userService.getUser(userId));
        Item item = ItemMapper.toItem(itemService.getItemById(bookingDto.getItemId()));
        item.setOwner(userId);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findByBookingIdAndUserId(long bookingId, long userId) {
        return BookingMapper.toBookingDto(bookingRepository.findByIdAndUserIdAndOwnerId(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException("Такого бронирования не найдено")));
    }

    @Override
    public BookingDto setApprove(long bookingId, long userId, boolean isApproved) {
        Booking booking = bookingRepository.findByIdAndItem_Owner(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException("Неверный запрос"));
        if (isApproved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidatorExceptions("Статус уже одобрен");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> findBookingsByUserByState(long userId, String state) {
        if (Objects.isNull(userService.getUser(userId))) {
            throw new UserNotFoundException("Такого польователя не существует");
        }
        if (state.equals(ALL.toString())) {
            return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(CURRENT.toString())) {
            return bookingRepository.findAllByBooker_IdAndStatusCurrent(userId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(FUTURE.toString())) {
            return bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByIdDesc(userId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(PAST.toString())) {
            return bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByIdDesc(userId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(WAITING.toString())) {
            return bookingRepository.findAllByBooker_IdAndByStatus(userId, WAITING.toString()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (state.equals(REJECTED.toString())) {
            return bookingRepository.findAllByBooker_IdAndByStatus(userId, REJECTED.toString()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            throw new ValidatorExceptions("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> findAllBookingsByOwner(long ownerId, String state) {
        if (Objects.isNull(userService.getUser(ownerId))) {
            throw new UserNotFoundException("Такого польователя не существует");
        }
        if (state.equals(ALL.toString())) {
            return bookingRepository.findAllByItem_OwnerOrderByStartDesc(ownerId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals(FUTURE.toString())) {
            return bookingRepository.findBookingsByOwnerAndStatusFuture(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals(PAST.toString())) {
            return bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByIdDesc(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals(WAITING.toString())) {
            return bookingRepository.findAllByOwner_IdAndByStatus(ownerId, WAITING.toString()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals(CURRENT.toString())) {
            return bookingRepository.findAllByItem_OwnerAndStatusCurrent(ownerId, LocalDateTime.now()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals(REJECTED.toString())) {
            return bookingRepository.findAllByOwner_IdAndByStatus(ownerId, REJECTED.toString()).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            throw new ValidatorExceptions("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}