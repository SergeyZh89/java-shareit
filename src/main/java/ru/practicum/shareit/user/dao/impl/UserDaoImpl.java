package ru.practicum.shareit.user.dao.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class UserDaoImpl implements UserDao {
    private long generatedId = 0;
    private final List<User> userList = new ArrayList<>();

    private long getGeneratedId() {
        return ++generatedId;
    }

    @Override
    public User addUser(User user) {
        user.setId(getGeneratedId());
        userList.add(user);
        return user;
    }

    @Override
    public User getUserById(long userId) {
        return getAllUsers().stream()
                .filter(user -> user.getId() == userId)
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
    }

    @Override
    public List<User> getAllUsers() {
        return userList;
    }

    @Override
    public User updateUserById(User userNew, long userId) {
        User user = new User();
        for (User userFound : userList) {
            if (userFound.getId() == userId) {
                if (userNew.getName() != null) {
                    userFound.setName(userNew.getName());
                }
                if (userNew.getEmail() != null) {
                    userFound.setEmail(userNew.getEmail());
                }
                user = userFound;
            }
        }
        return user;
    }

    @Override
    public void deleteUserById(long userId) {
        userList.removeIf(user -> user.getId() == userId);
    }
}
