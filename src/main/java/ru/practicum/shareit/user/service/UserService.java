package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.base.exception.EmailConflictException;
import ru.practicum.shareit.base.exception.NotFoundException;
import ru.practicum.shareit.base.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserRepository repository;

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
        var userWithSameEmail = repository.getAll().stream().filter(x -> x.getEmail().equals(email)).findFirst();
        if (user.getEmail() == null) {
            throw new ValidationException("Email не задан");
        } else if (user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }  else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @");
        }  else if (userWithSameEmail.isPresent()) {
            if (!Objects.equals(userWithSameEmail.get().getId(), user.getId())) {
                throw new EmailConflictException();
            }
        }
    }

    public User get(@PathVariable long id) {
        var object = repository.find(id);
        if (object.isEmpty()) {
            throw new NotFoundException(id, object.getClass().getSimpleName());
        }
        return object.get();
    }

    public List<User> getAll() {
        return repository.getAll();
    }

    public void remove(long id) {
        repository.remove(id);
    }

    public User create(@RequestBody User object) {
        validateUser(object);
        return repository.add(object);
    }

    public User patch(@RequestBody User object) {
        var original = get(object.getId());
        object = change(original, object);
        validateUser(object);
        return repository.change(object);
    }
}
