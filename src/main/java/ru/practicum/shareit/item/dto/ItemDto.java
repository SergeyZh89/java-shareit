package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;

    private long owner;

    private String name;

    private String description;

    private Boolean available;

    private long request;

    private ItemBooking lastBooking;

    private ItemBooking nextBooking;

    private List<ItemComments> comments;

    public ItemDto(long id, String name,
                   String description,
                   Boolean available,
                   long request,
                   ItemDto.ItemBooking lastBooking,
                   ItemDto.ItemBooking nextBooking,
                   List<ItemDto.ItemComments> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }

    @Data
    public static class ItemBooking {
        private final long id;

        private final long bookerId;
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

        private LocalDateTime created;
    }
}