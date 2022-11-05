package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : 0,
                new BookingShortDto(),
                new BookingShortDto(),
                new ArrayList<>());
    }

    public static ItemBookingDto toItemBookingDto(Item item) {
        return new ItemBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : 0,
                null,
                null,
                new ArrayList<>());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getOwner(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable());
    }
}