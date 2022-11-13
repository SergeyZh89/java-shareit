package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getItemRequest(long requestId);

    List<ItemRequestDto> getRequestsByUser(long userId);
}
