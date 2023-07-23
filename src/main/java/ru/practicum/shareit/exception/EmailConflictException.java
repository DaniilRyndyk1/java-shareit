package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.CONFLICT
)
public class EmailConflictException extends RuntimeException {

    public EmailConflictException() {
        super("Email уже существует");
    }
}