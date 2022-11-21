package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequest itemRequest = new ItemRequest().toBuilder()
            .description("itemsDescription")
            .created(LocalDateTime.of(1999, 11, 11, 11, 11))
            .build();

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingAnItemRequest() {
        Assertions.assertEquals(0, itemRequest.getId());
        em.persist(itemRequest);
        Assertions.assertEquals(1, itemRequest.getId());
    }

    @Test
    void verifyRepositorySaveAnItemRequest() {
        Assertions.assertEquals(0, itemRequest.getId());
        itemRequestRepository.save(itemRequest);
        Assertions.assertEquals(1, itemRequest.getId());
    }

    @Test
    void findAllByRequestor_Id() {
        User user = new User().toBuilder().name("Mike").email("mike@mail.ru").build();
        userRepository.save(user);
        itemRequest.setRequestor(userRepository.save(user));
        itemRequestRepository.save(itemRequest);

        List<ItemRequestDto> requestList = itemRequestRepository.findAllByRequestor_Id(1).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        Assertions.assertEquals(requestList.size(), 1);
    }

    @Test
    void findAllByOwner_Id() {
        UserDto userDto = new UserDto().toBuilder().name("Roman").email("roman@mail.ru").build();
        User user = userRepository.save(UserMapper.toUser(userDto));

        itemRequest.setRequestor(user);
        ItemRequest request = itemRequestRepository.save(itemRequest);

        Item item = new Item().toBuilder()
                .name("Дрель")
                .description("Новая")
                .available(true)
                .owner(1L)
                .request(request)
                .build();
        itemRepository.save(item);

        List<ItemRequestDto> list = itemRequestRepository.findAllByOwner_Id(1, Pageable.unpaged()).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(list.size(), 1);
    }
}