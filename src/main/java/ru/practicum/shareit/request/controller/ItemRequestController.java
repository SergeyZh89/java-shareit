package ru.practicum.shareit.request.controller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@NoArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на получение реквеста от пользователя: " + userId);
        return itemRequestService.getRequestByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsOtherUsers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на получение всех реквестов пользователя: " + userId);
        int page = from / size;
        PageRequest request = PageRequest.of(page, size);
        return itemRequestService.getRequestsByOwnerWithPagination(userId, request);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long requestId) {
        log.info("Получен запрос на получение реквеста :" + requestId);
        return itemRequestService.getItemRequestByUser(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание реквеста от пользователя: " + userId);
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }
}