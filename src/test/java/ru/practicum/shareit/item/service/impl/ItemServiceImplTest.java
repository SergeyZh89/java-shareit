package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final CommentRepository commentRepository;
    @Mock
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void addComment() {
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