package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
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
    public Item getItem(@PathVariable long itemId) {
        log.info("Получен запрос на вещь под номером: " + itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<Item> getItemsBySearch(@RequestParam String text) {
        log.info("Получен запрос на поиск вещей по названию");
        return itemService.searchItemsByText(text);
    }

    @GetMapping
    public List<Item> getItemsByUserId(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос на вещи под номером: " + userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-id") long userId,
                        @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи от пользователя: " + userId);
        return itemService.addItemByUserId(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-id") long userId, @RequestBody ItemDto itemDto,
                           @PathVariable long itemId) {
        log.info("Получен запрос на обновление вещи под номером: " + itemId);
        return itemService.updateItem(userId, itemDto, itemId);
    }
}
