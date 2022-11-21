package ru.practicum.shareit.booking.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.state.State.REJECTED;
import static ru.practicum.shareit.booking.state.State.WAITING;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserService userService,
                              ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto addNewBooking(long userId, BookingDto bookingDto) {
        if (!itemService.getItemById(bookingDto.getItemId()).getAvailable()) {
            throw new ValidatorExceptions("Данная вещь недоступна");
        }
        User user = UserMapper.toUser(userService.getUser(userId));
        if (userId == bookingDto.getItemId()) {
            throw new ItemNotFoundException("Владелец вещи не может бронировать свою вещь");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidatorExceptions("Время начала бронирования раньше времени окончания бронирования");
        }
        Item item = ItemMapper.toItem(itemService.getItemById(bookingDto.getItemId()));
        item.setOwner(userId);
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        Booking bookingSaved = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(bookingSaved);
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
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findBookingsByUserByState(long userId, String state, Pageable pageable) {
        if (Objects.isNull(userService.getUser(userId))) {
            throw new UserNotFoundException("Такого польователя не существует");
        }
        if (Arrays.stream(State.values()).noneMatch(st -> st.toString().equals(state))) {
            throw new ValidatorExceptions("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBooker_IdAndStatusCurrent(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByIdDesc(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByIdDesc(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndByStatus(userId, WAITING.toString(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndByStatus(userId, REJECTED.toString(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public List<BookingDto> findAllBookingsByOwner(long ownerId, String state, Pageable pageable) {
        if (Objects.isNull(userService.getUser(ownerId))) {
            throw new UserNotFoundException("Такого польователя не существует");
        }
        if (Arrays.stream(State.values()).noneMatch(st -> st.toString().equals(state))) {
            throw new ValidatorExceptions("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByItem_OwnerOrderByStartDesc(ownerId, pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByItem_OwnerAndStatusCurrent(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findBookingsByOwnerAndStatusFuture(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByIdDesc(ownerId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByOwner_IdAndByStatus(ownerId, WAITING.toString(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByOwner_IdAndByStatus(ownerId, REJECTED.toString(), pageable).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }
}