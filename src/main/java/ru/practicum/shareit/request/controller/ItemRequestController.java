package ru.practicum.shareit.request.controller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@NoArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsOtherUsers(@RequestParam long from,
                                                      @RequestParam int size) {
        return Collections.emptyList();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable long requestId) {
        return itemRequestService.getItemRequest(requestId);
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание реквеста от пользователя: " + userId);
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }
}