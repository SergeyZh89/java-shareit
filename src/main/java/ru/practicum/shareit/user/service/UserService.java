package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User addUser(UserDto userDto);

    User getUser(long userId);

    User updateUser(UserDto userDto, long userId);

    void deleteUser(long userId);
}
