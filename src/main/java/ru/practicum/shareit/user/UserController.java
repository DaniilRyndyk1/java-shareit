package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.base.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public User get(@PathVariable long id) {
        return service.get(id);
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.remove(id);
    }

    @PostMapping
    public User create(@RequestBody User object) {
        return service.create(object);
    }

    @PatchMapping("{id}")
    public User change(@RequestBody User object, @PathVariable long id) {
        object.setId(id);
        return service.patch(object);
    }

    @ExceptionHandler
    @ResponseStatus(
            value = HttpStatus.BAD_REQUEST,
            reason = "Данные не корректны"
    )
    public Map<String, String> handleWrongData(final ValidationException e) {
        return Map.of("error", e.getMessage());
    }

    public void validate(User object) {
        service.validate(object);
    }
}
