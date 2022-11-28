package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_REQUEST_HEAD = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@Positive @RequestHeader(USER_REQUEST_HEAD) long userId) {
        log.info("Получен запрос на получение реквеста от пользователя: " + userId);
        return itemRequestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsOtherUsers(@Positive @RequestHeader(USER_REQUEST_HEAD) long userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                        @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос на получение всех реквестов пользователя: " + userId);
        return itemRequestClient.getRequestsOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@Positive @RequestHeader(USER_REQUEST_HEAD) long userId,
                                             @Positive @PathVariable long requestId) {
        log.info("Получен запрос на получение реквеста :" + requestId);
        return itemRequestClient.getRequest(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Positive @RequestHeader(USER_REQUEST_HEAD) long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание реквеста от пользователя: " + userId);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }
}
