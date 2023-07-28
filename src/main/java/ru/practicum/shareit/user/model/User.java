package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.base.Model;

@Data
public class User extends Model {
    private String email;

    public User(String name, String email) {
        this.setName(name);
        this.setEmail(email);
    }
}
