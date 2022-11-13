package ru.practicum.shareit.item.controller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@NoArgsConstructor
@Slf4j
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-id") long userId,
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
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос на вещи пользователя под номером: " + userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-id") long userId,
                           @RequestBody ItemDto itemDto,
                           @RequestParam(required = false, defaultValue = "0") long requestId) {
        log.info("Получен запрос на добавление вещи от пользователя: " + userId);
        return itemService.addItemByUserId(itemDto, userId, requestId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария от: " + userId);
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-id") long userId, @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        log.info("Получен запрос на обновление вещи под номером: " + itemId);
        return itemService.updateItem(userId, itemDto, itemId);
    }
}