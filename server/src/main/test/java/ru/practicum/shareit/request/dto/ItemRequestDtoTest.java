package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDtoTest() throws IOException {
        ItemRequestDto itemRequestDto = new ItemRequestDto().toBuilder()
                .id(1L)
                .requestorId(1L)
                .description("itemsDescription")
                .created(LocalDateTime.of(1999, 11, 11, 11, 11))
                .items(List.of(new ItemRequestDto.ItemsRequest().toBuilder()
                        .id(2L)
                        .owner(2L)
                        .name("name")
                        .description("descriptionTest")
                        .available(true)
                        .requestId(2L)
                        .build()))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("itemsDescription");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("1999-11-11T11:11:00");
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .extracting("id")
                .contains(2);
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .extracting("owner")
                .contains(2);
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .extracting("name")
                .contains("name");
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .extracting("description")
                .contains("descriptionTest");
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .extracting("available")
                .contains(true);
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .extracting("requestId")
                .contains(2);
    }
}