package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {
    private long id;

    @NotNull(message = "Нужно ввести описание")
    private String description;

    private long requestorId;

    private LocalDateTime created;

    private List<ItemsRequest> items;

    @Getter
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