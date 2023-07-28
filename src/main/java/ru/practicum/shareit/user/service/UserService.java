package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User change(User original, User object) {
        if (object.getName() == null) {
            object.setName(original.getName());
        }
        if (object.getEmail() == null) {
            object.setEmail(original.getEmail());
        }
        return object;
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

    public User get(@PathVariable long id) {
        var object = repository.findById(id);
        if (object.isEmpty()) {
            throw new NotFoundException(id, object.getClass().getSimpleName());
        }
        return object.get();
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public void remove(long id) {
        var user = get(id);
        repository.delete(user);
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
}
