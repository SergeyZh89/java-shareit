package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";

    private final ItemDto itemDto = new ItemDto().toBuilder()
            .id(1L)
            .name("Дрель")
            .owner(1L)
            .description("новая")
            .available(true)
            .comments(new ArrayList<>())
            .lastBooking(new ItemDto.ItemBooking())
            .nextBooking(new ItemDto.ItemBooking())
            .build();

    @Test
    void getItem() throws Exception {
        when(itemService.getItemByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header(USER_REQUEST_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.owner", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Дрель")))
                .andExpect(jsonPath("$.description", is("новая")))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDto.getNextBooking().getBookerId()), Long.class));

        verify(itemService, Mockito.times(1)).getItemByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void getItemsBySearch() throws Exception {
        when(itemService.searchItemsByText(anyString()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", anyString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].owner", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("Дрель")))
                .andExpect(jsonPath("$[0].description", is("новая")));

        verify(itemService, Mockito.times(1)).searchItemsByText(anyString());
    }

    @Test
    void getItemsByUserId() throws Exception {
        when(itemService.getAllItemsByUserId(1L))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .param("text", anyString())
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].owner", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("Дрель")))
                .andExpect(jsonPath("$[0].description", is("новая")));

        verify(itemService, Mockito.times(1)).getAllItemsByUserId(1L);
    }

    @Test
    void testAddItem() throws Exception {
        when(itemService.addItemByUserId(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_REQUEST_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.owner", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Дрель")))
                .andExpect(jsonPath("$.description", is("новая")));

        verify(itemService, Mockito.times(1)).addItemByUserId(any(), anyLong());
    }

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = new CommentDto().toBuilder()
                .id(1L)
                .itemId(2L)
                .authorId(3L)
                .authorName("name")
                .text("textTest")
                .created(LocalDateTime.of(1999, 11, 11, 11, 11))
                .build();
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", "1")
                        .header(USER_REQUEST_HEADER, 3L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.itemId", is(2L), Long.class))
                .andExpect(jsonPath("$.authorId", is(3L), Long.class))
                .andExpect(jsonPath("$.authorName", is("name")))
                .andExpect(jsonPath("$.text", is("textTest")))
                .andExpect(jsonPath("$.created", is("11.11.1999 11:11")));

        verify(itemService, Mockito.times(1)).addComment(anyLong(), anyLong(), any());
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemDtoUpdated = new ItemDto().toBuilder()
                .name("nameUpdate")
                .available(false)
                .description("descriptionUpdate")
                .build();
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(itemDtoUpdated);

        mockMvc.perform(patch("/items/{itemid}", 1)
                        .header(USER_REQUEST_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDtoUpdated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("nameUpdate")))
                .andExpect(jsonPath("$.available", is(false)))
                .andExpect(jsonPath("$.description", is("descriptionUpdate")));

        verify(itemService, Mockito.times(1))
                .updateItem(anyLong(), any(ItemDto.class), anyLong());
    }
}