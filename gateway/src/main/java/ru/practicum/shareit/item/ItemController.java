package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@Slf4j
@Validated
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";


    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@Positive @RequestHeader(USER_REQUEST_HEADER) long userId,
                                          @Positive @PathVariable long itemId) {
        log.info("Получен запрос на вещь под номером: " + itemId + " пользователя: " + userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam String text) {
        log.info("Получен запрос на поиск вещей по названию");
        return itemClient.getItemsBySearch(text);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@Positive @RequestHeader(USER_REQUEST_HEADER) long userId) {
        log.info("Получен запрос на вещи пользователя под номером: " + userId);
        return itemClient.getItemsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@Positive @RequestHeader(USER_REQUEST_HEADER) long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи от пользователя: " + userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive @RequestHeader(USER_REQUEST_HEADER) long userId,
                                             @Positive @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария: " + itemId + " от: " + userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader(USER_REQUEST_HEADER) long userId, @RequestBody ItemDto itemDto,
                                             @Positive @PathVariable long itemId) {
        log.info("Получен запрос на обновление вещи под номером: " + itemId);
        return itemClient.updateItem(userId, itemDto, itemId);
    }
}