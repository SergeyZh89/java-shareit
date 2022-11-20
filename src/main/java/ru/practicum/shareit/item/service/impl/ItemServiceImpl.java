package ru.practicum.shareit.item.service.impl;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        if (userId == 0) {
            throw new ValidatorExceptions("Неверный запрос");
        }
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
            ItemDto.ItemComments itemComments = new ItemDto.ItemComments();
            itemComments.setCreated(LocalDateTime.now());
            itemComments.setAuthorName(user.getName());
            itemComments.setAuthorId(userId);
            itemComments.setItemId(itemId);
            itemDto.getComments().add(itemComments);
        } else {
            throw new ValidatorExceptions("У пользователя нет брони");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto));
        commentDto.setId(comment.getId());
        return commentDto;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи не существует")));
    }

    @Override
    public ItemDto getItemByIdAndUserId(long userId, long itemId) {
        List<Booking> bookingslist = bookingRepository.findByItem_IdAndItem_Owner(itemId, userId);
        List<ItemDto.ItemComments> itemCommentsList = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toItemComment)
                .collect(Collectors.toList());
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи не существует")));
        if (!bookingslist.isEmpty()) {
            Booking nextBooking = bookingslist.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min((Comparator.comparing(Booking::getStart))).orElse(null);
            Booking lastBooking = bookingslist.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .max((Comparator.comparing(Booking::getEnd))).orElse(null);
            if (nextBooking != null) {
                itemDto.setNextBooking(new ItemDto.ItemBooking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            if (lastBooking != null) {
                itemDto.setLastBooking(new ItemDto.ItemBooking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
        }
        itemDto.setOwner(userId);
        itemDto.setComments(itemCommentsList);
        return itemDto;
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
                itemDto.setNextBooking(new ItemDto.ItemBooking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            if (lastBooking != null) {
                itemDto.setLastBooking(new ItemDto.ItemBooking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
        }
        return itemDtoList;
    }

    @Override
    public ItemDto addItemByUserId(ItemDto itemDto, long userId) {
        if (userId <= 0 || userRepository.findAll().stream()
                .filter(user -> user.getId() == userId).findAny().isEmpty()) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }

        if (itemDto.getAvailable() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null) {
            throw new ValidatorExceptions("Неверные данные");
        }

        itemDto.setOwner(userId);
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != 0) {
            item.setRequest(itemRequestRepository
                    .findById(itemDto.getRequestId()).orElse(null));
        }
        itemRepository.save(item);
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        Item itemFound = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Такой вещи не существует"));
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
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (!text.isBlank()) {
            itemDtoList = itemRepository.searchAllByText(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return itemDtoList;
    }
}