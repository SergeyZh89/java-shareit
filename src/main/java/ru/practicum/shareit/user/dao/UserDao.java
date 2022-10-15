package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDao {
    User addUser(User user);

    List<User> getAllUsers();

    User getUserById(long userId);

    User updateUserById(User user, long userid);

    void deleteUserById(long userId);
}
