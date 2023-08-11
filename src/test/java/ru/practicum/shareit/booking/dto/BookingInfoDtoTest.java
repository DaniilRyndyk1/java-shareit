package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInfoDtoTest {
    @Autowired
    private JacksonTester<BookingInfoDto> json;
    private final BookingInfoDto bookingShortDto = new BookingInfoDto(1L, 2L);

    @Test
    void testJsonBookingShortDto() throws Exception {
        JsonContent<BookingInfoDto> result = json.write(bookingShortDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }
}
