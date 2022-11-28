package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(USER_REQUEST_HEADER) long userId,
                           @PathVariable long itemId) {
        log.info("Получен запрос на вещь под номером: " + itemId + " пользователя: " + userId);
        return itemService.getItemByIdAndUserId(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        log.info("Получен запрос на поиск вещей по названию");
        return itemService.searchItemsByText(text);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(USER_REQUEST_HEADER) long userId) {
        log.info("Получен запрос на вещи пользователя под номером: " + userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_REQUEST_HEADER) long userId,
                           @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи от пользователя: " + userId);
        return itemService.addItemByUserId(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_REQUEST_HEADER) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария: " + itemId + " от: " + userId);
        return itemService.addComment(userId, itemId, commentDto);
    }

    @Validated
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_REQUEST_HEADER) long userId, @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        log.info("Получен запрос на обновление вещи под номером: " + itemId);
        return itemService.updateItem(userId, itemDto, itemId);
    }
}