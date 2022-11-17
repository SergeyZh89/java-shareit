package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDTO() throws IOException {
        ItemDto itemDto = new ItemDto().toBuilder()
                .id(1L)
                .owner(1L)
                .comments(List.of(new ItemDto.ItemComments(1L,
                        "textTest",
                        1L,
                        1L,
                        "authorName",
                        LocalDateTime.of(1999, 11, 11, 11, 11))))
                .name("testItem")
                .description("testDedcription")
                .available(true)
                .requestId(4L)
                .lastBooking(new ItemDto.ItemBooking(1L, 1L))
                .nextBooking(new ItemDto.ItemBooking(2L, 2L))
                .build();
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .extracting("id").contains(1);
        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .extracting("text").contains("textTest");
        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .extracting("itemId").contains(1);
        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .extracting("authorId").contains(1);
        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .extracting("authorName").contains("authorName");
        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .extracting("created").contains("11.11.1999 11:11");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("testItem");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDedcription");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(4);
        assertThat(result).extractingJsonPathValue("$.lastBooking")
                .extracting("id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking")
                .extracting("bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.nextBooking")
                .extracting("id").isEqualTo(2);
        assertThat(result).extractingJsonPathValue("$.nextBooking")
                .extracting("bookerId").isEqualTo(2);
    }
}