package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {

    @Autowired
    private final EntityManager em;

    @Autowired
    private final UserService service;

    @Test
    void testGetUsers() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("Ivan", "ivan@email"),
                makeUserDto("Petr", "petr@email"),
                makeUserDto("Vasilii", "vasilii@email")
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        List<UserDto> targetUsers = service.getUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void getUser() {
        UserDto userDto = makeUserDto("Petr", "user@email.ru");
        User user = UserMapper.toUser(userDto);
        em.persist(user);
        em.flush();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User targetUser = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(targetUser.getId(), equalTo(1L));
        assertThat(targetUser.getEmail(), equalTo(userDto.getEmail()));
        assertThat(targetUser.getName(), equalTo(user.getName()));
    }

    @Test
    void addUser() {
        UserDto userDto = makeUserDto("Petr", "user@email.ru");
        service.addUser(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        UserDto userDto = makeUserDto("Petr", "user@email.ru");
        service.addUser(userDto);
        UserDto userDtoUpdate = makeUserDto("Anatoliy", "user@email.ru");
        service.updateUser(userDtoUpdate, 1);
        UserDto user = service.getUser(1);
        assertThat(user.getName(), equalTo("Anatoliy"));
    }

    @Test
    void deleteUser() {
        UserDto userDto = makeUserDto("Petr", "user@email.ru");
        service.addUser(userDto);
        service.deleteUser(1);
        List<UserDto> userDtos = service.getUsers();
        assertThat(userDtos, empty());
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}