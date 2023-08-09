package ru.practicum.shareit.handler;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionFailedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ErrorHandlerTest {
    private final ErrorHandler handler;

    @Test
    void shouldHandleNotFoundException() {
        var message = "Объект не найден";
        var response = handler.handlerNotFoundException(new NotFoundException(message));
        assertEquals(response.getError(), message);
    }

    @Test
    void shouldHandleValidationException() {
        var message = "Ошибка валидации";
        var response = handler.handleValidationException(new ValidationException(message));
        assertEquals(response.getError(), message);
    }

    @Test
    void shouldHandleConversionFailedException() {
        var message = "Неизвестное состояние";
        var response = handler.handleConversionFailedException(new ConversionFailedException(null, null, message, null));
        assertNotNull(response);
    }
}
