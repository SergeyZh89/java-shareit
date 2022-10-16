package ru.practicum.shareit.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemDao.getItemById(itemId));
    }

    @Override
    public ItemDto addItemByUserId(ItemDto itemDto, long userId) {
        if (userId <= 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        } else {
            userDao.getAllUsers().stream()
                    .filter(user -> user.getId() == userId)
                    .findAny()
                    .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidatorExceptions("Неверные данные");
        }
        if (itemDto.getName().isEmpty()) {
            throw new ValidatorExceptions("Неверные данные");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidatorExceptions("Неверные данные");
        }
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemDao.addItemByUserId(item, userId));
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        if (itemId <= 0 || userId <= 0) {
            throw new ItemNotFoundException("Такой вещи не существует");
        }
        List<Item> itemList = itemDao.getAllItemsByUserId(userId);
        itemList.stream()
                .filter(item -> item.getId() == itemId)
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи у пользователя не существует"));
        itemDao.getAllItems().stream()
                .filter(item -> item.getId() == itemId)
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи не существует"));
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemDao.updateItem(item, itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        return itemDao.getAllItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return itemDao.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}