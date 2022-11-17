package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exceptions.ValidatorExceptions;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final UserDto userDto = new UserDto().toBuilder()
            .id(1L)
            .name("Mike")
            .email("mike@mail.ru")
            .build();

    @Test
    void testGetUsers() throws Exception {
        when(userService.getUsers())
                .thenReturn(Arrays.asList(userDto, new UserDto()
                        .toBuilder()
                        .id(2L)
                        .name("Bob")
                        .email("bob@mail.ru")
                        .build()));

        mockMvc.perform(get("/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("Mike")))
                .andExpect(jsonPath("$[0].email", is("mike@mail.ru")))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].name", is("Bob")))
                .andExpect(jsonPath("$[1].email", is("bob@mail.ru")));

        Mockito.verify(userService, Mockito.times(1)).getUsers();
    }

    @Test
    void testGetUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Mike")))
                .andExpect(jsonPath("$.email", is("mike@mail.ru")));

        Mockito.verify(userService, Mockito.times(1)).getUser(anyLong());
    }

    @Test
    void testAddUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Mockito.verify(userService, times(1)).addUser(any());
    }

    @Test
    void testAddUserWithUserNotFoundException() throws Exception {
        when(userService.addUser(any()))
                .thenThrow(new UserNotFoundException("Такого пользователя не существует"));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void testAddUserWithEmailExistException() throws Exception {
        when(userService.addUser(any()))
                .thenThrow(new EmailAlreadyExistsException("Такой емайл уже существует"));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(409));
    }

    @Test
    void testAddUserWithValidatorException() throws Exception {
        when(userService.addUser(any()))
                .thenThrow(new ValidatorExceptions("Неверные данные"));

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));

        Mockito.verify(userService, Mockito.times(1)).addUser(any());
    }

    @Test
    void testUpdateUser() throws Exception {
        userDto.setName("Bob");
        userDto.setEmail("bob@mail.ru");
        when(userService.updateUser(userDto, 1))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.name", is("Bob")))
                .andExpect(jsonPath("$.email", is("bob@mail.ru")))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).updateUser(userDto, 1);
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(anyLong());
    }
}