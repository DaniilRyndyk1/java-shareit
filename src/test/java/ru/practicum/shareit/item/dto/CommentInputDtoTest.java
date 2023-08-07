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
public class CommentInputDtoTest {
    private JacksonTester<CommentInputDto> json;
    private final Validator validator;
    private final CommentInputDto commentDto = new CommentInputDto("Test1");

    public CommentInputDtoTest(@Autowired JacksonTester<CommentInputDto> json) {
        this.json = json;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testJsonItemDto() throws Exception {
        JsonContent<CommentInputDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Test1");
    }

    @Test
    void whenCommentInputDtoIsValidThenViolationsShouldBeEmpty() {
        assertThat(validator.validate(commentDto, Create.class)).isEmpty();
    }

    @Test
    void whenCommentInputDtoTextNotNullThenViolationsShouldBeReportedNotNull() {
        commentDto.setText(null);
        Set<ConstraintViolation<CommentInputDto>> violations = validator.validate(commentDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be blank'");
    }

    @Test
    void whenCommentInputDtoTextNotBlankThenViolationsShouldBeReportedNotBlank() {
        commentDto.setText("");
        Set<ConstraintViolation<CommentInputDto>> violations = validator.validate(commentDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be blank'");
    }
}
