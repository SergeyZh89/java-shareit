package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ItemDto {
    private long id;
    private long owner;
    private String name;
    private String description;
    private Boolean available;
    private Long request;

    public ItemDto(String name, String description, Boolean available, long request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}