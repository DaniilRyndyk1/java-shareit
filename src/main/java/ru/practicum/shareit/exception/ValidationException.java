package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        reason = "Объект не найден"
)
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super("При создании объекта возникло исключение: " + message);
    }
}
