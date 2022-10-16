package ru.practicum.shareit.booking.item.service;

import ru.practicum.shareit.booking.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(long itemId);

    ItemDto addItemByUserId(ItemDto itemDto, long userId);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> getAllItemsByUserId(long userId);

    List<ItemDto> searchItemsByText(String text);
}
