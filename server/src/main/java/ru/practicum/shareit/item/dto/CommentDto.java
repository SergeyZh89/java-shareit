package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    private long id;

    private String text;

    private long itemId;

    private long authorId;

    private String authorName;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime created;
}