package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.status.Status;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws IOException {
        BookingDto bookingDto = new BookingDto().toBuilder()
                .id(1L)
                .itemId(1L)
                .item(new BookingDto.Item().toBuilder()
                        .id(1L)
                        .name("testName")
                        .build())
                .start(LocalDateTime.of(2000, 11, 11, 11, 11))
                .end(LocalDateTime.of(2000, 11, 11, 11, 12))
                .status(Status.APPROVED)
                .booker(new BookingDto.User().toBuilder()
                        .id(1L)
                        .build())
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("testName");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2000-11-11T11:11:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2000-11-11T11:12:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
    }
}