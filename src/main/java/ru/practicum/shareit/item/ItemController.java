package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Config;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService service;

    @GetMapping("{id}")
    public ItemDtoWithBooking get(@PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.getWithBookings(id, userId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAll(@RequestHeader(Config.userHeaderName) Long userId) {
        return service.getAllWithBookings(userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        service.remove(id);
    }

    @PostMapping
    public ItemDto create(@Validated(Create.class) @RequestBody ItemDto dto, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.create(dto, userId);
    }

    @PatchMapping("{id}")
    public ItemDto change(@Validated(Update.class) @RequestBody ItemDto dto, @PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.patch(dto, id, userId);
    }

    @GetMapping("search")
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return service.search(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentInputDto dto, @PathVariable Long itemId, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.createComment(dto, itemId, userId);
    }
}

