package ru.practicum.shareit.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.CONFLICT
)
public class EmailConflictException extends RuntimeException {

    public EmailConflictException() {
        super(String.format("Email уже существует"));
    }
}