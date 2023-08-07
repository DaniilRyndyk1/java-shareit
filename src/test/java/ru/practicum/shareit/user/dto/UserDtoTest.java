package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    private JacksonTester<UserDto> json;
    private final Validator validator;
    private final UserDto userDto = new UserDto(1L, "Danila", "111@ya.ru");

    public UserDtoTest(@Autowired JacksonTester<UserDto> json) {
        this.json = json;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testJsonItemDto() throws Exception {
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Danila");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("111@ya.ru");

    }

    @Test
    void whenUserDtoIsValidThenViolationsShouldBeEmpty() {
        assertThat(validator.validate(userDto, Create.class)).isEmpty();
    }

    @Test
    void whenUserDtoNameNotNullThenViolationsShouldBeReportedNotNull() {
        userDto.setName(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно быть пустым'");
    }

    @Test
    void whenUserDtoNameNotBlankThenViolationsShouldBeReportedNotBlank() {
        userDto.setName("");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно быть пустым'");
    }

    @Test
    void whenUserDtoEmailNotNullThenViolationsShouldBeReportedNotNull() {
        userDto.setEmail(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно быть пустым'");
    }

    @Test
    void whenUserDtoEmailNotBlankThenViolationsShouldBeReportedNotBlank() {
        userDto.setEmail("");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class, Update.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно быть пустым'");
    }
}
