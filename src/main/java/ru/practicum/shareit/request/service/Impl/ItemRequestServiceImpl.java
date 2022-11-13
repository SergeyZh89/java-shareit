package ru.practicum.shareit.request.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        return ItemRequestMapper
                .toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequest(long requestId) {
        return ItemRequestMapper
                .toItemRequestDto(itemRequestRepository
                        .findById(requestId)
                        .orElseThrow(() -> new ItemRequestNotFoundException("Такого реквеста не существует")));
    }

    @Override
    public List<ItemRequestDto> getRequestsByUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует"));
        return itemRequestRepository.findAllByRequestor_Id(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
