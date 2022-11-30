package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
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