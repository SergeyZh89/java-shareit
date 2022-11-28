package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получен запрос на список пользователей");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@Positive @PathVariable long userId) {
        log.info("Получен запрос на пользователя под номером: {}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping()
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя: {}", userDto.getName());
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @Positive @PathVariable long userId) {
        log.info("Получен запрос на обновление пользователя под номером: " + userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя под номером: " + userId);
        return userClient.deleteUser(userId);
    }
}