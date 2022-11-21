package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.Status;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private static final String USER_REQUEST_HEADER = "X-Sharer-User-id";

    private BookingDto bookingDto = new BookingDto().toBuilder()
            .id(1L)
            .itemId(1L)
            .item(new BookingDto.Item().toBuilder()
                    .id(1L)
                    .name("testName")
                    .build())
            .status(Status.APPROVED)
            .booker(new BookingDto.User().toBuilder()
                    .id(1L)
                    .build())
            .build();

    @Test
    void addNewBooking() throws Exception {
        when(bookingService.addNewBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_REQUEST_HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.itemId", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("testName")))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService, times(1)).addNewBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    void findByBookingIdAndUserId() throws Exception {
        when(bookingService.findByBookingIdAndUserId(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_REQUEST_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.itemId", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("testName")))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService, times(1)).findByBookingIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void findAllBookingsByOwner() throws Exception {
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(bookingService.findAllBookingsByOwner(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_REQUEST_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(1L), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].item.name", is("testName")))
                .andExpect(jsonPath("$[0].booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].status", is("APPROVED")));

        verify(bookingService, times(1)).findAllBookingsByOwner(anyLong(), anyString(), any());
    }

    @Test
    void findBookingsByUserIdByState() throws Exception {
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(bookingService.findBookingsByUserByState(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header(USER_REQUEST_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService, times(1))
                .findBookingsByUserByState(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void setApprove() throws Exception {
        when(bookingService.setApprove(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", "1")
                        .header(USER_REQUEST_HEADER, 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.itemId", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("testName")))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService, times(1)).setApprove(anyLong(), anyLong(), anyBoolean());
    }
}