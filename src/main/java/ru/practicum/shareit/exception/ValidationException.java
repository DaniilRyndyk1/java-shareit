package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        reason = "Ошибка валидации"
)
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
