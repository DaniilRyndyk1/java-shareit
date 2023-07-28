package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.base.Model;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
public class ItemRequest extends Model {
    private String description;
    private User requestor;
    private LocalDate created;

    public ItemRequest(String description, User requestor, LocalDate created) {
        this.setDescription(description);
        this.setRequestor(requestor);
        this.setCreated(created);
    }
}
