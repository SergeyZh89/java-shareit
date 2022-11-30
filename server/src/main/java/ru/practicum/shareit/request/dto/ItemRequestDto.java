package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {
    private long id;

    private String description;

    private long requestorId;

    private LocalDateTime created;

    private List<ItemsRequest> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class ItemsRequest {
        private long id;
        private long owner;
        private String name;
        private String description;
        private boolean available;
        private long requestId;
    }
}