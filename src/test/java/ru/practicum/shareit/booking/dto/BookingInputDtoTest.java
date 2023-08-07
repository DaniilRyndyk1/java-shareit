package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInputDtoTest {
    private JacksonTester<BookingInputDto> json;
    private final Validator validator;
    private final LocalDateTime start = LocalDateTime.now().plusHours(1);
    private final LocalDateTime end = LocalDateTime.now().plusHours(2);
    private final BookingInputDto bookingInputDto = new BookingInputDto(start, end, 1L);


    public BookingInputDtoTest(@Autowired JacksonTester<BookingInputDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testJsonBookingInputDto() throws Exception {
        JsonContent<BookingInputDto> result = json.write(bookingInputDto);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void whenBookingInputDtoIsValidThenViolationsShouldBeEmpty() {
        assertThat(validator.validate(bookingInputDto)).isEmpty();
    }

    @Test
    void whenBookingInputDtoItemIdNotNullThenViolationsShouldBeReportedNotNull() {
        bookingInputDto.setItemId(null);
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно равняться null'");
    }

    @Test
    void whenBookingInputDtoStartNotNullThenViolationsShouldBeReportedNotNull() {
        bookingInputDto.setStart(null);
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно равняться null'");
    }

    @Test
    void whenBookingInputDtoEndNotNullThenViolationsShouldBeReportedNotNull() {
        bookingInputDto.setEnd(null);
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно равняться null'");
    }

    @Test
    void whenBookingInputDtoStartBeforeNowThenViolationsShouldBeReportedNotNull() {
        bookingInputDto.setStart(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        System.out.println(violations);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='должно содержать сегодняшнее " +
                "число или дату, которая еще не наступила");
    }

    @Test
    void whenBookingInputDtoEndBeforeNowThenViolationsShouldBeReportedNotNull() {
        bookingInputDto.setEnd(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='должно содержать дату, " +
                "которая еще не наступила'");
    }
}