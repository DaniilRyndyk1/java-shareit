package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;

    @GetMapping("{id}")
    public User get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        service.remove(id);
    }

    @PostMapping
    public User create(@RequestBody User object) {
        return service.create(object);
    }

    @PatchMapping("{id}")
    public User change(@RequestBody User user, @PathVariable Long id) {
        user.setId(id);
        return service.patch(user);
    }
}
