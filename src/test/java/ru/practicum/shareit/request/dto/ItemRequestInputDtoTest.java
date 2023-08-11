package ru.practicum.shareit.request.dto;

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
public class ItemRequestInputDtoTest {
    private JacksonTester<ItemRequestInputDto> json;
    private final Validator validator;
    private final ItemRequestInputDto commentDto = new ItemRequestInputDto("Test1");

    public ItemRequestInputDtoTest(@Autowired JacksonTester<ItemRequestInputDto> json) {
        this.json = json;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testJsonItemDto() throws Exception {
        JsonContent<ItemRequestInputDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test1");
    }

    @Test
    void whenRequestItemInputDtoIsValidThenViolationsShouldBeEmpty() {
        assertThat(validator.validate(commentDto, Create.class)).isEmpty();
    }

    @Test
    void whenRequestItemInputDtoDescriptionNotNullThenViolationsShouldBeReportedNotNull() {
        commentDto.setDescription(null);
        Set<ConstraintViolation<ItemRequestInputDto>> violations = validator.validate(commentDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be blank'");
    }

    @Test
    void whenRequestItemInputDtoDescriptionNotBlankThenViolationsShouldBeReportedNotBlank() {
        commentDto.setDescription("");
        Set<ConstraintViolation<ItemRequestInputDto>> violations = validator.validate(commentDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be blank'");
    }
}
