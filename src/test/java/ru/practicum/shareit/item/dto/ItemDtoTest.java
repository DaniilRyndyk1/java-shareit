package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.groups.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    private JacksonTester<ItemDto> json;
    private final Validator validator;
    private final ItemDto itemDto = new ItemDto(1L, "Item1", "Description1", true,  null);

    public ItemDtoTest(@Autowired JacksonTester<ItemDto> json) {
        this.json = json;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testJsonItemDto() throws Exception {
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description1");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(null);
    }

    @Test
    void whenItemDtoIsValidThenViolationsShouldBeEmpty() {
        assertThat(validator.validate(itemDto, Create.class)).isEmpty();
    }

    @Test
    void whenItemDtoNameNotNullThenViolationsShouldBeReportedNotNull() {
        itemDto.setName(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно быть пустым'");
    }

    @Test
    void whenItemDtoNameNotBlankThenViolationsShouldBeReportedNotBlank() {
        itemDto.setName("");
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно быть пустым'");
    }

    @Test
    void whenItemDtoDescriptionNotNullThenViolationsShouldBeReportedNotNull() {
        itemDto.setDescription(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно быть пустым'");
    }

    @Test
    void whenItemDtoDescriptionNotBlankThenViolationsShouldBeReportedNotBlank() {
        itemDto.setDescription("");
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        System.out.println(violations);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно быть пустым");
    }

    @Test
    void whenItemDtoAvailableNotNullThenViolationsShouldBeReportedNotNull() {
        itemDto.setAvailable(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно равняться null'");
    }
}
