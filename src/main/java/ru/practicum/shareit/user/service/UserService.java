package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto addUser(UserDto userDto);

    UserDto getUser(long userId);

    UserDto updateUser(UserDto userDto, long userId);

    void deleteUser(long userId);
}