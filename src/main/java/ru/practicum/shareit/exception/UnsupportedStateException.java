package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException(String state) {
        super("Unknown state: " + state);
    }
}
