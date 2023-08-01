package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User change(User original, User user) {
        if (user.getName() == null) {
            user.setName(original.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(original.getEmail());
        }
        return user;
    }

    public User get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public void remove(Long id) {
        repository.delete(get(id));
    }

    public User create(User user) {
        validateUser(user);
        return repository.save(user);
    }

    public User patch(User user) {
        var original = get(user.getId());
        user = change(original, user);
        validateUser(user);
        return repository.save(user);
    }

    private void validateUser(User user) {
        String email = user.getEmail();
        if (email == null) {
            throw new ValidationException("Email не задан");
        } else if (email.isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }  else if (!email.contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
    }
}
