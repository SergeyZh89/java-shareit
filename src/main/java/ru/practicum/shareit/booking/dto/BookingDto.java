package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.status.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class BookingDto {
    private long id;

    @FutureOrPresent(message = "Время бронирования не должен быть в прошлом")
    private LocalDateTime start;

    @Future(message = "Время бронирования не должен быть в прошлом")
    private LocalDateTime end;

    private long itemId;

    private BookingDto.Item item;

    private BookingDto.User booker;

    private Status status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private long id;

        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private long id;
    }
}