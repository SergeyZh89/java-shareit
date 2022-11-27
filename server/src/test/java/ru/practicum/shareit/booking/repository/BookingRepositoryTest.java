package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private User user = new User().toBuilder()
            .name("Mike")
            .email("mike@mail.ru")
            .build();

    private Item item = new Item().toBuilder()
            .name("Дрель")
            .owner(1L)
            .available(true)
            .description("Простая дрель")
            .build();


    private Booking booking = new Booking().toBuilder()
            .item(item)
            .build();


    @Test
    void contextLoads() {
        assertNotNull(em);
    }


    @Test
    void findByIdAndUserIdAndOwnerId() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        Booking targetBooking = bookingRepository
                .findByIdAndUserIdAndOwnerId(booking.getId(), user.getId())
                .orElseThrow();

        Assertions.assertEquals(booking.getItem().getName(), targetBooking.getItem().getName());
    }

    @Test
    void findByItem_Id() {
        em.persist(user);
        em.persist(item);
        em.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findByItem_Id(item.getId());

        Assertions.assertEquals(1, targetBooking.size());
    }
}