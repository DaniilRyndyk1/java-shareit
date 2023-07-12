package ru.practicum.shareit.base.exception;

import ru.practicum.shareit.base.Model;

public class ValidationException extends RuntimeException {
    private final Model model;

    public Model getModel() {
        return model;
    }

    public ValidationException(String message, Model model) {
        super("При создании объекта типа " + model.getClass().getSimpleName() + ":" + message);
        this.model = model;
    }
}
