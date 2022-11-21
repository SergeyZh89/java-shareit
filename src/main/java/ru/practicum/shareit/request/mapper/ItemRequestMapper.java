package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                new ArrayList<>());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                new User(itemRequestDto.getId(), null, null),
                itemRequestDto.getCreated());
    }

    public static ItemRequestDto.ItemsRequest toItemsRequest(Item item) {
        return new ItemRequestDto.ItemsRequest(item.getId(),
                item.getOwner(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId());
    }
}