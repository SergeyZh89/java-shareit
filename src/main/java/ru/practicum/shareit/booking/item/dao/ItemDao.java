package ru.practicum.shareit.booking.item.dao;

import ru.practicum.shareit.booking.item.model.Item;

import java.util.List;

public interface ItemDao {

    Item getItemById(long itemId);

    Item addItemByUserId(Item item, long userId);

    Item updateItem(Item item, long itemId);

    List<Item> getAllItemsByUserId(long userId);

    List<Item> getAllItems();

    List<Item> searchItemsByText(String text);
}