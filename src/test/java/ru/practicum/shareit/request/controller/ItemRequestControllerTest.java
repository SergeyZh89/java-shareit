package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto.ItemsRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";

    private ItemRequestDto itemRequestDto = new ItemRequestDto().toBuilder()
            .id(1L)
            .requestorId(1L)
            .description("itemsDescription")
            .created(LocalDateTime.of(1999, 11, 11, 11, 11))
            .items(List.of(new ItemsRequest().toBuilder()
                    .id(2L)
                    .owner(2L)
                    .name("name")
                    .description("descriptionTest")
                    .available(true)
                    .requestId(2L)
                    .build()))
            .build();

    @Test
    void getRequestsByUser() throws Exception {
        List<ItemRequestDto> sourceList = List.of(itemRequestDto);
        when(itemRequestService.getRequestByUser(anyLong()))
                .thenReturn(sourceList);

        mockMvc.perform(get("/requests")
                        .header(USER_REQUEST_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].requestorId", is(1L), Long.class))
                .andExpect(jsonPath("$[0].description", is("itemsDescription")))
                .andExpect(jsonPath("$[0].created", is("1999-11-11T11:11:00")))
                .andExpect(jsonPath("$[0].items", hasSize(1)));

        verify(itemRequestService, times(1)).getRequestByUser(anyLong());
    }

    @Test
    void getRequestsOtherUsers() throws Exception {
        List<ItemRequestDto> sourceList = List.of(itemRequestDto);

        Pageable pageable = PageRequest.of(0, 1);

        when(itemRequestService.getRequestsByOwnerWithPagination(anyLong(), eq(pageable)))
                .thenReturn(sourceList);

        mockMvc.perform(get("/requests/all")
                        .header(USER_REQUEST_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].requestorId", is(1L), Long.class))
                .andExpect(jsonPath("$[0].description", is("itemsDescription")))
                .andExpect(jsonPath("$[0].created", is("1999-11-11T11:11:00")))
                .andExpect(jsonPath("$[0].items", hasSize(1)));

        verify(itemRequestService, times(1))
                .getRequestsByOwnerWithPagination(anyLong(), eq(pageable));
    }

    @Test
    void getRequest() throws Exception {
        when(itemRequestService.getItemRequestByUser(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", "1")
                        .header(USER_REQUEST_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.requestorId", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("itemsDescription")))
                .andExpect(jsonPath("$.created", is("1999-11-11T11:11:00")))
                .andExpect(jsonPath("$.items", hasSize(1)));

        verify(itemRequestService, times(1)).getItemRequestByUser(anyLong(), anyLong());
    }

    @Test
    void addItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_REQUEST_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.requestorId", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is("itemsDescription")))
                .andExpect(jsonPath("$.created", is("1999-11-11T11:11:00")))
                .andExpect(jsonPath("$.items", hasSize(1)));

        verify(itemRequestService, times(1))
                .addItemRequest(anyLong(), any(ItemRequestDto.class));

    }
}