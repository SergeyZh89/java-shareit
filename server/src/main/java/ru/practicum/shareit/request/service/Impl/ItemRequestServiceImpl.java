package ru.practicum.shareit.request.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemRepository itemRepository,
                                  UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper
                .toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequestByUser(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Такого реквеста не существует"));
        List<ItemRequestDto.ItemsRequest> itemList = itemRepository.findAllByRequest_Id(requestId).stream()
                .map(ItemRequestMapper::toItemsRequest)
                .collect(Collectors.toList());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemList);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getRequestByUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
        List<ItemRequestDto> list = itemRequestRepository.findAllByRequestor_Id(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : list) {
            List<ItemRequestDto.ItemsRequest> itemList = itemRepository.findAllByRequest_Id(itemRequestDto.getId()).stream()
                    .map(ItemRequestMapper::toItemsRequest)
                    .collect(Collectors.toList());
            itemRequestDto.setItems(itemList);
        }
        return list;
    }

    @Override
    public List<ItemRequestDto> getRequestsByOwnerWithPagination(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
        List<ItemRequestDto> list = itemRequestRepository.findAllByOwner_Id(userId, pageable).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : list) {
            List<ItemRequestDto.ItemsRequest> itemList = itemRepository.findAllByRequest_Id(itemRequestDto.getId()).stream()
                    .map(ItemRequestMapper::toItemsRequest)
                    .collect(Collectors.toList());
            itemRequestDto.setItems(itemList);
        }
        return list;
    }
}