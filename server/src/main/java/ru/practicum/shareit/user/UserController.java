package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;

    @GetMapping("{id}")
    public UserDto get(@PathVariable Long id) {
        return service.getDto(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        service.remove(id);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        return service.create(user);
    }

    @PatchMapping("{id}")
    public UserDto change(@RequestBody UserDto user, @PathVariable Long id) {
        user.setId(id);
        return service.patch(user);
    }
}
