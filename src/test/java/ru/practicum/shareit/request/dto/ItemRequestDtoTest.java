package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private final LocalDateTime created = LocalDateTime.now();
    private final ItemRequestDto commentDto = new ItemRequestDto(1L, "Test", created, null);

    @Test
    void testJsonBookingShortDto() throws Exception {
        JsonContent<ItemRequestDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(null);
    }
}
