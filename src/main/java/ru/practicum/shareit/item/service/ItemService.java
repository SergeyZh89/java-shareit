package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(long itemId);

    Item addItemByUserId(ItemDto itemDto, long userId);

    Item updateItem(long userId, ItemDto itemDto, long itemId);

    List<Item> getAllItemsByUserId(long userId);

    List<Item> searchItemsByText(String text);
}
