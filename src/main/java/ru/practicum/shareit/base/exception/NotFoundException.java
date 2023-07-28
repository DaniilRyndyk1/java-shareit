package ru.practicum.shareit.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        reason = "Объект не найден"
)
public class NotFoundException extends RuntimeException {

    public NotFoundException(long id, String name) {
        super(String.format("Не удалось найти {0} id = {1}", name, id));
    }
}