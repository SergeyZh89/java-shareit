package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemByIdAndUserId(long userId, long itemId);

    ItemDto getItemById(long itemId);

    ItemDto addItemByUserId(ItemDto itemDto, long userId, long requestId);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> getAllItemsByUserId(long userId);

    List<ItemDto> searchItemsByText(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}