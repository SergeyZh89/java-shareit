package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.LenientStubber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
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

//    @Autowired
//    private final ItemService itemService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final ItemRequestService itemRequestService;

//    @Mock
//    private ItemService itemService;
//    @Mock
//    private ItemRepository itemRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private CommentRepository commentRepository;
//    @Mock
//    BookingRepository bookingRepository;


    @Test
    void addComment() {
        ItemServiceImpl itemService = new ItemServiceImpl();
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "userRepository", userRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        Comment comment = new Comment().toBuilder().id(1L).text("text").itemId(1L).authorId(1L).authorName("author").created(LocalDateTime.now()).build();
        ItemDto itemDto = new ItemDto().toBuilder().id(1L).owner(1L).name("name").description("description").build();
        Item item = new Item().toBuilder().id(1).owner(1L).name("name").description("description").build();
        User user = new User().toBuilder().name("Mike").email("mike@mail.ru").build();
        Booking booking = new Booking().toBuilder()
                .id(1)
                .item(item)
                .status(Status.APPROVED)
                .build();
        booking.setStart(LocalDateTime.of(2000, 11, 11, 11, 11));
        booking.setEnd(LocalDateTime.of(2000, 11, 11, 11, 12));

        lenient()
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        lenient()
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        lenient()
                .when(bookingRepository.findByBooker_Id(anyLong()))
                .thenReturn(List.of(booking));

        lenient()
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        itemService.addComment(1L, 1L, CommentMapper.toCommentDto(comment));
    }

    @Test
    void getItemById() {
    }

    @Test
    void getItemByIdAndUserId() {
    }

    @Test
    void getAllItemsByUserId() {
    }

    @Test
    void addItemByUserId() {
    }

    @Test
    void updateItem() {
    }

    @Test
    void searchItemsByText() {
    }
}