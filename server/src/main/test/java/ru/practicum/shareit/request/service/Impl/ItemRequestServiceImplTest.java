package ru.practicum.shareit.request.service.Impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    @Autowired
    private final EntityManager em;

    @Autowired
    private final ItemRequestService itemRequestService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private final UserService userService;

    private ItemRequestDto itemRequestDto = new ItemRequestDto().toBuilder()
            .requestorId(1L)
            .description("itemsDescription")
            .created(LocalDateTime.of(1999, 11, 11, 11, 11))
            .items(List.of(new ItemRequestDto.ItemsRequest().toBuilder()
                    .name("itemName")
                    .description("descriptionTest")
                    .requestId(1L)
                    .owner(2L)
                    .build()))
            .build();

    @Test
    void addItemRequest() {
        userService.addUser(new UserDto().toBuilder().name("Roman").email("roman@mail.ru").build());
        itemRequestService.addItemRequest(1, itemRequestDto);
        TypedQuery<ItemRequest> query = em
                .createQuery("select i from ItemRequest i where i.description = :descr", ItemRequest.class);
        ItemRequest targetItemRequest = query
                .setParameter("descr", itemRequestDto.getDescription())
                .getSingleResult();
        assertThat(targetItemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(targetItemRequest.getId(), notNullValue());
        assertThat(targetItemRequest.getRequestor().getId(), equalTo(1L));
    }

    @Test
    void getItemRequestByUser() {
        userService.addUser(new UserDto().toBuilder().name("Roman").email("roman@mail.ru").build());
        itemRequestService.addItemRequest(1, itemRequestDto);
        ItemRequestDto targetItemRequest = itemRequestService.getItemRequestByUser(1, 1);
        assertThat(targetItemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(targetItemRequest.getId(), notNullValue());
        assertThat(targetItemRequest.getRequestorId(), equalTo(1L));
        assertThat(targetItemRequest.getItems(), notNullValue());
    }

    @Test
    void getWrongItemRequestByUser() {
        userService.addUser(new UserDto().toBuilder().name("Roman").email("roman@mail.ru").build());
        itemRequestService.addItemRequest(1, itemRequestDto);

        Throwable throwable = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestByUser(1, 0));
        assertNotNull(throwable.getMessage());
    }

    @Test
    void getRequestByUser() {
        userService.addUser(new UserDto().toBuilder().name("Roman").email("roman@mail.ru").build());
        itemRequestService.addItemRequest(1, itemRequestDto);
        List<ItemRequestDto> requestDtoList = List.of(itemRequestDto);
        List<ItemRequestDto> targetItemRequestDto = itemRequestService.getRequestByUser(1);
        assertThat(targetItemRequestDto, hasSize(1));
        for (ItemRequestDto requestDto : requestDtoList) {
            assertThat(targetItemRequestDto, hasItem(allOf(hasProperty("id", notNullValue()),
                    hasProperty("requestorId", is(1L)),
                    hasProperty("description", equalTo(requestDto.getDescription())))));
        }
    }

    @Test
    void getRequestsByUserWithPagination() {
        UserDto userDto = new UserDto().toBuilder().name("Roman").email("roman@mail.ru").build();
        userService.addUser(userDto);

        itemRequestService.addItemRequest(1L, itemRequestDto);

        ItemDto itemDto = new ItemDto().toBuilder()
                .description("новая")
                .name("дрель")
                .available(true)
                .requestId(1)
                .build();
        itemService.addItemByUserId(itemDto, 1);

        List<ItemRequestDto> targetItemRequestDto = itemRequestService
                .getRequestsByOwnerWithPagination(1, Pageable.unpaged());
        assertThat(targetItemRequestDto, hasSize(1));
        for (ItemRequestDto requestDto : targetItemRequestDto) {
            assertThat(targetItemRequestDto, hasItem(allOf(hasProperty("id", notNullValue()),
                    hasProperty("requestorId", is(1L)),
                    hasProperty("description", equalTo(requestDto.getDescription())))));
        }
    }
}