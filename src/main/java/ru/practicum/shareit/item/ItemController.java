package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.base.exception.NotFoundException;
import ru.practicum.shareit.base.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService service;
    private final UserService userService;

    public ItemController(ItemService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ItemDto get(@PathVariable long id) {
        return service.get(id).toDto();
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getAll(userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.remove(id);
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto object, @RequestHeader("X-Sharer-User-Id") long userId) {
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        var item = object.toItem(user);
        return service.create(item).toDto();
    }

    @PatchMapping("{id}")
    public ItemDto change(@RequestBody ItemDto object, @PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        var item = service.get(id);
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException(userId, "Пользователь не является владельцем");
        }
        object.setId(id);
        return service.patch(object.toItem(null)).toDto();
    }

    @ExceptionHandler
    @ResponseStatus(
            value = HttpStatus.BAD_REQUEST,
            reason = "Данные не корректны"
    )
    public Map<String, String> handleWrongData(final ValidationException e) {
        return Map.of("error", e.getMessage());
    }

    @GetMapping("search")
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return service.search(text);
    }
}

