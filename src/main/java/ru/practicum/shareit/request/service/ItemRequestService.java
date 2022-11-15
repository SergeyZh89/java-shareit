package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getItemRequestByUser(long userId, long requestId);

    List<ItemRequestDto> getRequestByUser(long userId);

    List<ItemRequestDto> getRequestsByUserWithPagination(long userId, Pageable pageable);
}