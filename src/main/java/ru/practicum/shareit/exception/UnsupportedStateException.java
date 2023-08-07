package ru.practicum.shareit.exception;

import ru.practicum.shareit.booking.enums.State;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException(State state) {
        super("Unknown state: " + state.name());
    }
}
