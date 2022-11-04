package ru.practicum.shareit.user.service.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long userId) {
        if (userId <= 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return UserMapper.toUserDto(userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует")));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User userNew = UserMapper.toUser(userDto);
        if (userDto.getEmail() == null || !userDto.getEmail().contains("@")) {
            throw new ValidatorExceptions("Введены неверные данные");
        }
        return UserMapper.toUserDto(userRepository.save(userNew));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
        if (userId <= 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        User userNew = UserMapper.toUser(userDto);
        userNew.setId(userId);
        if (userNew.getName() != null) {
            user.setName(userNew.getName());
        }
        if (userNew.getEmail() != null) {
            user.setEmail(userNew.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
