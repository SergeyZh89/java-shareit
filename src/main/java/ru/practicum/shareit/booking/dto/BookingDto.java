package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.status.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private long id;

    @FutureOrPresent(message = "Время бронирования не должен быть в прошлом")
    private LocalDateTime start;

    @Future(message = "Время бронирования не должен быть в прошлом")
    private LocalDateTime end;

    private long itemId;

    private Item item;

    private User booker;

    private Status status;

    @Data
    public static class Item {
        private final long id;

        private final String name;
    }

    @Data
    public static class User {
        private final long id;
    }
}