package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.Create;
import ru.practicum.shareit.group.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient client;

    @GetMapping("{id}")
    public ResponseEntity<Object> get(@PathVariable Long id) {
        log.info("Get user {}", id);
        return client.get(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get users");
        return client.getAll();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Remove user {}", id);
        return client.delete(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto user) {
        log.info("Creating user {}", user);
        return client.create(user);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> change(@Validated(Update.class) @RequestBody UserDto user, @PathVariable Long id) {
        log.info("Changing user {}", user);
        user.setId(id);
        return client.change(user);
    }
}
