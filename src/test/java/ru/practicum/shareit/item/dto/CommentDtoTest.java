package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private final LocalDateTime created = LocalDateTime.now();
    private final CommentDto commentDto = new CommentDto(1L, "Test", "Danila", created);

    @Test
    void testJsonBookingShortDto() throws Exception {
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Danila");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
