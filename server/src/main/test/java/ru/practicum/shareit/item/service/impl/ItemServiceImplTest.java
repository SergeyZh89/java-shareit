package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Autowired
    private final EntityManager em;

    @Autowired
    private final ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookingRepository bookingRepository;

    private User user = new User().toBuilder()
            .id(1L)
            .name("Mike")
            .email("mike@mail.ru")
            .build();

    private Item item = new Item().toBuilder()
            .id(1L)
            .available(true)
            .owner(1L)
            .name("Дрель")
            .description("Новая")
            .request(new ItemRequest().toBuilder()
                    .id(1L)
                    .description("testDecr")
                    .requestor(user)
                    .created(LocalDateTime.now())
                    .build())
            .build();

    private Comment comment = new Comment().toBuilder()
            .id(1L)
            .text("text")
            .itemId(1L)
            .authorId(1L)
            .authorName("author")
            .created(LocalDateTime.now()).build();

    private Booking booking = new Booking().toBuilder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();

    @Test
    void addComment() {
        booking.setStart(LocalDateTime.of(2000, 11, 11, 11, 11));
        booking.setEnd(LocalDateTime.of(2000, 11, 11, 11, 12));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(bookingRepository.findByBooker_Id(1L))
                .thenReturn(List.of(booking));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        Throwable throwableWithoutBookingUser = assertThrows(ValidatorExceptions.class,
                () -> itemService.addComment(2L, 1L, CommentMapper.toCommentDto(comment)));
        assertNotNull(throwableWithoutBookingUser.getMessage());

        Throwable throwableUserId = assertThrows(ValidatorExceptions.class,
                () -> itemService.addComment(0, 1L, CommentMapper.toCommentDto(comment)));
        assertNotNull(throwableUserId.getMessage());

        comment.setText("text");

        itemService.addComment(1L, 1L, CommentMapper.toCommentDto(comment));

        verify(itemRepository, times(3)).findById(anyLong());
        verify(userRepository, times(3)).findById(anyLong());
        verify(bookingRepository, times(3)).findByBooker_Id(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void getItemById() {
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        ItemDto targetItem = itemService.getItemById(1);

        Assertions.assertEquals(targetItem.getName(), item.getName());
        Assertions.assertEquals(targetItem.getDescription(), item.getDescription());
        Assertions.assertEquals(targetItem.getId(), item.getId());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemByIdAndUserId() {
        booking.setStart(LocalDateTime.of(2000, 11, 11, 11, 11));
        booking.setEnd(LocalDateTime.of(2000, 11, 11, 11, 12));

        Mockito
                .when(bookingRepository.findByItem_IdAndItem_Owner(anyLong(), anyLong()))
                .thenReturn(List.of(booking));

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    long itemId = invocationOnMock.getArgument(0, Long.class);
                    if (itemId <= 0) {
                        throw new ItemNotFoundException("Такой вещи не существует");
                    } else {
                        return Optional.ofNullable(item);
                    }
                });

        ItemDto targetItem = itemService.getItemByIdAndUserId(1L, 1L);

        Assertions.assertEquals(targetItem.getName(), item.getName());
        Assertions.assertEquals(targetItem.getDescription(), item.getDescription());
        Assertions.assertEquals(targetItem.getId(), item.getId());

        Throwable thrown = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemByIdAndUserId(1L, 0));
        assertNotNull(thrown.getMessage());

        verify(itemRepository, times(2)).findById(anyLong());
        verify(bookingRepository, times(2)).findByItem_IdAndItem_Owner(anyLong(), anyLong());
    }

    @Test
    void getAllItemsByUserId() {
        item.setId(1L);
        item.setOwner(1L);
        List<Item> sourceList = List.of(item);
        List<ItemDto> sourceDtoList = sourceList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        Mockito
                .when(itemRepository.findAll())
                .thenReturn(sourceList);

        List<ItemDto> targetList = itemService.getAllItemsByUserId(user.getId());

        verify(itemRepository, times(1)).findAll();

        assertThat(targetList, hasSize(1));

        for (ItemDto itemDto : sourceDtoList) {
            assertThat(targetList, hasItem(allOf(
                    hasProperty("name", equalTo(itemDto.getName())),
                    hasProperty("description", equalTo(itemDto.getDescription()))
            )));
        }
    }

    @Test
    void addItemByUserId() {
        List<User> userList = List.of(user);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(userList);

        itemService.addItemByUserId(ItemMapper.toItemDto(item), 1L);

        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItem() {
        item.setId(1L);
        item.setOwner(1L);
        List<Item> sourceList = List.of(item);

        Item itemOther = new Item().toBuilder()
                .id(2L)
                .available(true)
                .owner(2L)
                .name("Пила")
                .description("Ручная")
                .request(new ItemRequest().toBuilder()
                        .id(1L)
                        .description("testDecr")
                        .requestor(user)
                        .created(LocalDateTime.now())
                        .build())
                .build();

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(itemRepository.findAll())
                .thenReturn(sourceList);

        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(itemOther);

        itemService.updateItem(1L, ItemMapper.toItemDto(itemOther), 1);

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void searchItemsByText() {
        List<Item> sourceList = List.of(item);
        Mockito
                .when(itemRepository.searchAllByText(anyString()))
                .thenReturn(sourceList);

        List<ItemDto> targetList = itemService.searchItemsByText("Дрель");

        for (Item sourceItem : sourceList) {
            assertThat(targetList, hasItem(allOf(
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription()))
            )));
        }

        verify(itemRepository, times(1)).searchAllByText(anyString());
    }
}