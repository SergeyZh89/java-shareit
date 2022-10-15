package ru.practicum.shareit.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> getUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User getUser(long userId) {
        if (userId <= 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return userDao.getUserById(userId);
    }

    @Override
    public User addUser(UserDto userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().contains("@")) {
            throw new ValidatorExceptions("Введены неверные данные");
        }
        User userNew = UserMapper.toUser(userDto);
        for (User user : userDao.getAllUsers()) {
            if (user.getEmail() != null && user.getEmail().equals(userNew.getEmail())) {
                throw new EmailAlreadyExistsException("Пользователь с такой почтой уже существует");
            }
        }
        return userDao.addUser(userNew);
    }

    @Override
    public User updateUser(UserDto userDto, long userId) {
        if (userId <= 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        User userNew = UserMapper.toUser(userDto);
        for (User user : userDao.getAllUsers()) {
            if (user.getEmail() != null && user.getEmail().equals(userNew.getEmail())) {
                throw new EmailAlreadyExistsException("Пользователь с такой почтой уже существует");
            }
        }
        return userDao.updateUserById(userNew, userId);
    }

    @Override
    public void deleteUser(long userId) {
        userDao.deleteUserById(userId);
    }
}
