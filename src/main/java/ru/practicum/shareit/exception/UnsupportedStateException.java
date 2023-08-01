package ru.practicum.shareit.exception;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException(String state) {
        super("Unknown state: " + state);
    }
}
