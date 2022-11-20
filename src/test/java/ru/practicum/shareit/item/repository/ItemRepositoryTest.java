package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private Item item = new Item().toBuilder()
            .name("Дрель")
            .description("Новая")
            .available(true)
            .build();

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void searchAllByText() {
        em.persist(item);
        List<Item> list = itemRepository.searchAllByText("Дрель");
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void findAllByRequest_Id() {
        em.persist(item);
        List<Item> list = itemRepository.findAllByRequest_Id(1);
        Assertions.assertEquals(0, list.size());
    }
}