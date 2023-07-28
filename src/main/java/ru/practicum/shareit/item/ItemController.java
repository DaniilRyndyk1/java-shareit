package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.base.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final String userHeaderName = "X-Sharer-User-Id";
    private final ItemService service;

    @GetMapping("{id}")
    public ItemDto get(@PathVariable long id) {
        return service.get(id).toDto();
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(userHeaderName) long userId) {
        return service.getAll(userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.remove(id);
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto object, @RequestHeader(userHeaderName) long userId) {
        return service.create(object, userId).toDto();
    }

    @PatchMapping("{id}")
    public ItemDto change(@RequestBody ItemDto object, @PathVariable long id, @RequestHeader(userHeaderName) long userId) {
        return service.patch(object.toItem(null), id, userId).toDto();
    }

    @GetMapping("search")
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return service.search(text);
    }

    @ExceptionHandler
    @ResponseStatus(
            value = HttpStatus.BAD_REQUEST,
            reason = "Данные не корректны"
    )
    public Map<String, String> handleWrongData(final ValidationException e) {
        return Map.of("error", e.getMessage());
    }
}

