package ru.practicum.shareit.item.service.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    public ItemServiceImpl(@Lazy ItemRepository itemRepository,
                           @Lazy UserRepository userRepository,
                           @Lazy BookingRepository bookingRepository,
                           @Lazy CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        ItemDto itemDto = itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new ItemNotFoundException("Такая вещь не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));
        if (commentDto.getText().isEmpty()) {
            throw new ValidatorExceptions("Нельзя оставлять поле комментария пустым");
        }
        if (bookingRepository.findByBooker_Id(userId).stream()
                .anyMatch(booking -> booking.getItem().getId() == itemDto.getId()
                        && !booking.getStatus().equals(Status.REJECTED)
                        && booking.getEnd().isBefore(LocalDateTime.now()))) {
            commentDto.setCreated(LocalDateTime.now());
            commentDto.setAuthorName(user.getName());
            commentDto.setAuthorId(userId);
            commentDto.setItemId(itemId);
            itemDto.getComments().add(commentDto);
        } else {
            throw new ValidatorExceptions("У пользователя нет брони");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto)));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи не существует")));
    }

    @Override
    public ItemBookingDto getItemByIdAndUserId(long userId, long itemId) {
        List<Booking> bookingslist = bookingRepository.findByItem_IdAndItem_Owner(itemId, userId);
        List<CommentDto> commentDtoList = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи не существует")));
        if (!bookingslist.isEmpty()) {
            Booking nextBooking = bookingslist.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min((Comparator.comparing(Booking::getStart))).orElse(null);
            Booking lastBooking = bookingslist.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .max((Comparator.comparing(Booking::getEnd))).orElse(null);
            if (nextBooking != null) {
                itemBookingDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
            }
            if (lastBooking != null) {
                itemBookingDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
            }
        }
        itemBookingDto.setOwner(userId);
        itemBookingDto.setComments(commentDtoList);
        return itemBookingDto;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        List<ItemDto> itemDtoList = itemRepository.findAll().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
        for (ItemDto itemDto : itemDtoList) {
            List<Booking> bookingList = bookingRepository.findByItem_Id(itemDto.getId());
            Booking nextBooking = bookingList.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min((Comparator.comparing(Booking::getStart))).orElse(null);
            Booking lastBooking = bookingList.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .max((Comparator.comparing(Booking::getEnd))).orElse(null);
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
            } else {
                itemDto.setNextBooking(null);
            }
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
            } else {
                itemDto.setLastBooking(null);
            }
        }
        return itemDtoList;
    }

    @Override
    public ItemDto addItemByUserId(ItemDto itemDto, long userId) {
        if (userId <= 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        } else {
            userRepository.findAll().stream()
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
        itemDto.setOwner(userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        Item itemFound = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи не существует"));
        if (itemId <= 0 || userId <= 0) {
            throw new ItemNotFoundException("Такой вещи не существует");
        }
        List<ItemDto> itemList = getAllItemsByUserId(userId);
        itemList.stream()
                .filter(item -> item.getId() == itemId)
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи у пользователя не существует"));
        Item item = ItemMapper.toItem(itemDto);
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
        return ItemMapper.toItemDto(itemRepository.save(itemFound));
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : itemRepository.findAll()) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();
            if ((name.contains(text.toLowerCase()) || description.contains(text.toLowerCase()))
                    && item.getAvailable() && !text.isBlank()) {
                itemList.add(item);
            }
        }
        return itemList.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}