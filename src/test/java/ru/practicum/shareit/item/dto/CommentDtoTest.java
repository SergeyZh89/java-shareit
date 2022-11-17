package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws IOException {
        CommentDto commentDto = new CommentDto().toBuilder()
                .id(1L)
                .text("textTest")
                .itemId(1L)
                .authorName("authorName")
                .authorId(1L)
                .build();
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("textTest");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("authorName");
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(1);
    }
}