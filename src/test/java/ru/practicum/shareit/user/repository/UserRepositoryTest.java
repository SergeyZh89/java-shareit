package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DataJpaTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    private final User user = new User().toBuilder()
            .name("Mike")
            .email("mike@mail.ru")
            .build();

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingAnUser() {
        Assertions.assertEquals(0, user.getId());
        em.persist(user);
        Assertions.assertEquals(1, user.getId());
    }

    @Test
    void verifyRepositorySaveAnUser() {
        Assertions.assertEquals(0, user.getId());
        userRepository.save(user);
        Assertions.assertEquals(1, user.getId());
    }

    @Test
    void verifyRepositoryGetAnUser() {
        User userOther = new User().toBuilder()
                .name("Bob")
                .email("bob@mail.ru")
                .build();
        em.persist(user);
        em.persist(userOther);
        Assertions.assertEquals(2, userOther.getId());
        Assertions.assertEquals("Bob", userOther.getName());
        Assertions.assertEquals("bob@mail.ru", userOther.getEmail());
    }

    @Test
    void verifyRepositoryGetAllUsers() {
        User userOther = new User().toBuilder()
                .name("Bob")
                .email("bob@mail.ru")
                .build();
        em.persist(user);
        em.persist(userOther);
        List<User> list = userRepository.findAll();
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void verifyRepositoryUpdateAnUser() {
        User userOther = new User().toBuilder()
                .id(1L)
                .name("Bob")
                .email("bob@mail.ru")
                .build();
        em.persist(user);
        userRepository.save(userOther);
        User currentUser = userRepository.findById(1L).get();
        Assertions.assertEquals(1, currentUser.getId());
        Assertions.assertEquals("Bob", currentUser.getName());
        Assertions.assertEquals("bob@mail.ru", currentUser.getEmail());
    }

    @Test
    void verifyRepositoryDeleteAnUser() {
        User userOther = new User().toBuilder()
                .name("Bob")
                .email("bob@mail.ru")
                .build();
        em.persist(user);
        em.persist(userOther);
        userRepository.deleteById(2L);
        Assertions.assertEquals(1, userRepository.findAll().size());
        Throwable thrown = assertThrows(UserNotFoundException.class,
                () -> userRepository.findById(2L)
                        .orElseThrow(() -> new UserNotFoundException("Пользователя не существует")));
        assertNotNull(thrown.getMessage());
    }
}