package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.status.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {
    private long id;

    @FutureOrPresent(message = "Время старта бронирования не должно быть в прошлом")
    private LocalDateTime start;

    @Future(message = "Время окончания бронирования не должен быть в прошлом")
    private LocalDateTime end;

    private long itemId;

    private Item item;

    private User booker;

    private Status status;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Item {
        private long id;

        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class User {
        private long id;
    }
}