package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private long id;

    private long owner;

    @NotEmpty(message = "Нужно указать имя")
    private String name;

    @NotEmpty(message = "Нужно указать описание")
    private String description;

    @NotNull
    private Boolean available;

    private long requestId;

    private ItemBooking lastBooking;

    private ItemBooking nextBooking;

    private List<ItemComments> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemBooking {
        private long id;

        private long bookerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemComments {
        private long id;

        private String text;

        private long itemId;

        private long authorId;

        private String authorName;

        @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
        private LocalDateTime created;
    }
}