package ru.practicum.shareit.base.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super("При создании объекта возникло исключение: " + message);
    }
}
