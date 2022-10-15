package ru.practicum.shareit.item.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ItemDaoImpl implements ItemDao {
    private long generatorId = 0;
    private final List<Item> items = new ArrayList<>();

    @Override
    public Item getItemById(long itemId) {
        return items.stream().
                filter(item -> item.getId() == itemId)
                .findAny().orElseThrow(() -> new ItemNotFoundException("Такой вещи не существует"));
    }

    @Override
    public Item addItemByUserId(Item item, long userId) {
        item.setId(getGeneratorId());
        item.setOwner(userId);
        items.add(item);
        return item;
    }

    @Override
    public List<Item> getAllItems() {
        return items;
    }

    @Override
    public Item updateItem(Item item, long itemId) {
        Item itemNew = new Item();
        for (Item itemFound : items) {
            if (itemFound.getId() == itemId) {
                if (item.getName() != null) {
                    itemFound.setName(item.getName());
                    }
                if (item.getDescription() != null) {
                    itemFound.setDescription(item.getDescription());
                }
                if (item.getOwner() != 0) {
                    itemFound.setOwner(item.getOwner());
                }
                if (item.getRequest() != null) {
                    itemFound.setRequest(item.getRequest());
                }
                if (item.getAvailable() != null) {
                    itemFound.setAvailable(item.getAvailable());
                }
                itemNew = itemFound;
            }
        }
        return itemNew;
    }

    @Override
    public List<Item> getAllItemsByUserId(long userId) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner() == userId) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();
            if ((name.contains(text.toLowerCase()) || description.contains(text.toLowerCase())) && item.getAvailable() && !text.isBlank()){
                itemList.add(item);
            }
        }
        return itemList;
    }

    public long getGeneratorId() {
        return ++generatorId;
    }
}