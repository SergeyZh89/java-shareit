package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item getItemById(long itemId);

    Item addItemByUserId(ItemDto itemDto, long userId);

    Item updateItem(Item item, long itemId);

    List<Item> getAllItemsByUserId(long userId);

    List<Item> getAllItems();

    List<Item> searchItemsByText(String text);
}
