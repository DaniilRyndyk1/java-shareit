package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public User toUser(UserDto user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }
}
