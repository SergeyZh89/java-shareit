package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    private Comment comment = new Comment().toBuilder()
            .authorName("Mike")
            .itemId(1L)
            .build();

    @Test
    void verifyBootstrappingByPersistingByItemId() {
        em.persist(comment);
        List<Comment> list = commentRepository.findByItemId(1L);
        Assertions.assertEquals(1, list.size());
    }

    @Test
    void verifyRepositoryByPersistingByItemId() {
        commentRepository.save(comment);
        List<Comment> list = commentRepository.findByItemId(1L);
        Assertions.assertEquals(1, list.size());
    }
}