package ru.practicum.shareit.booking.service.Impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.state.State;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    private User user = new User().toBuilder()
            .name("Mike")
            .email("mike@mail.ru")
            .build();

    private Item item = new Item().toBuilder()
            .id(1L)
            .available(true)
            .owner(1L)
            .name("Дрель")
            .description("Новая")
            .request(new ItemRequest().toBuilder()
                    .id(1L)
                    .description("testDecr")
                    .requestor(user)
                    .created(LocalDateTime.now())
                    .build())
            .build();

    private Booking booking = new Booking().toBuilder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(Status.WAITING)
            .build();

    @Test
    void testAddNewBooking() {
        booking.setStart(LocalDateTime.of(2000, 11, 11, 11, 11));
        booking.setEnd(LocalDateTime.of(2000, 11, 11, 11, 12));

        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        bookingService.addNewBooking(2L, BookingMapper.toBookingDto(booking));

        Throwable throwableItem = assertThrows(ItemNotFoundException.class,
                () -> bookingService.addNewBooking(1L, BookingMapper.toBookingDto(booking)));
        assertNotNull(throwableItem.getMessage());

        booking.setStart(LocalDateTime.of(2000, 11, 11, 11, 13));
        Throwable throwableDate = assertThrows(ItemNotFoundException.class,
                () -> bookingService.addNewBooking(1L, BookingMapper.toBookingDto(booking)));
        assertNotNull(throwableDate.getMessage());

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(userRepository, times(3)).findById(anyLong());
        verify(itemRepository, times(4)).findById(anyLong());
    }

    @Test
    void testAddNewBookingWrondUser() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Throwable throwableDate = assertThrows(UserNotFoundException.class,
                () -> bookingService.addNewBooking(1L, BookingMapper.toBookingDto(booking)));
        assertNotNull(throwableDate.getMessage());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void findByBookingIdAndUserId() {
        Mockito
                .when(bookingRepository.findByIdAndUserIdAndOwnerId(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    long bookingId = invocationOnMock.getArgument(0, Long.class);
                    if (bookingId <= 0) {
                        throw new BookingNotFoundException("Такого реквеста не существует");
                    } else {
                        return Optional.ofNullable(booking);
                    }
                });

        BookingDto targetBooking = bookingService.findByBookingIdAndUserId(1L, 1L);

        assertThat(targetBooking.getId(), equalTo(booking.getId()));
        assertThat(targetBooking.getItemId(), equalTo(booking.getItem().getId()));
        assertThat(targetBooking.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(targetBooking.getItem().getName(), equalTo(booking.getItem().getName()));

        Throwable throwable = assertThrows(BookingNotFoundException.class,
                () -> bookingService.findByBookingIdAndUserId(0, 1L));

        assertNotNull(throwable.getMessage());

        verify(bookingRepository, times(2)).findByIdAndUserIdAndOwnerId(anyLong(), anyLong());
    }

    @Test
    void setApprove() {
        Mockito
                .when(bookingRepository.findByIdAndItem_Owner(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        bookingService.setApprove(1L, 1L, true);

        verify(bookingRepository, times(1)).findByIdAndItem_Owner(anyLong(), anyLong());
    }

    @Test
    void findBookingsByUserByState() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        bookingService.findBookingsByUserByState(1L, State.ALL.toString(), Pageable.unpaged());
        bookingService.findBookingsByUserByState(1L, State.WAITING.toString(), Pageable.unpaged());
        bookingService.findBookingsByUserByState(1L, State.FUTURE.toString(), Pageable.unpaged());
        bookingService.findBookingsByUserByState(1L, State.CURRENT.toString(), Pageable.unpaged());
        bookingService.findBookingsByUserByState(1L, State.REJECTED.toString(), Pageable.unpaged());
        bookingService.findBookingsByUserByState(1L, State.PAST.toString(), Pageable.unpaged());

        verify(userRepository, times(6)).findById(anyLong());
    }

    @Test
    void findAllBookingsByOwner() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        bookingService.findAllBookingsByOwner(1L, State.ALL.toString(), Pageable.unpaged());
        bookingService.findAllBookingsByOwner(1L, State.WAITING.toString(), Pageable.unpaged());
        bookingService.findAllBookingsByOwner(1L, State.FUTURE.toString(), Pageable.unpaged());
        bookingService.findAllBookingsByOwner(1L, State.CURRENT.toString(), Pageable.unpaged());
        bookingService.findAllBookingsByOwner(1L, State.REJECTED.toString(), Pageable.unpaged());
        bookingService.findAllBookingsByOwner(1L, State.PAST.toString(), Pageable.unpaged());

        verify(userRepository, times(6)).findById(anyLong());
    }
}