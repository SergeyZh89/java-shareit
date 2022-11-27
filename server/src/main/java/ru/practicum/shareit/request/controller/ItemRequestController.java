package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_REQUEST_HEAD = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemRequestDto> getRequestsByUser(@RequestHeader(USER_REQUEST_HEAD) long userId) {
        log.info("Получен запрос на получение реквеста от пользователя: " + userId);
        return itemRequestService.getRequestByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsOtherUsers(@RequestHeader(USER_REQUEST_HEAD) long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос на получение всех реквестов пользователя: " + userId);
        int page = from / size;
        PageRequest request = PageRequest.of(page, size);
        return itemRequestService.getRequestsByOwnerWithPagination(userId, request);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader(USER_REQUEST_HEAD) long userId,
                                     @PathVariable long requestId) {
        log.info("Получен запрос на получение реквеста :" + requestId);
        return itemRequestService.getItemRequestByUser(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(USER_REQUEST_HEAD) long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание реквеста от пользователя: " + userId);
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }
}