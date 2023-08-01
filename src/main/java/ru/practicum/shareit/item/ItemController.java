package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final String userHeaderName = "X-Sharer-User-Id";
    private final ItemService service;

    @GetMapping("{id}")
    public ItemDtoWithBooking get(@PathVariable Long id, @RequestHeader(userHeaderName) Long userId) {
        return service.getWithBookings(id, userId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAll(@RequestHeader(userHeaderName) Long userId) {
        return service.getAllWithBookings(userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        service.remove(id);
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto dto, @RequestHeader(userHeaderName) Long userId) {
        return service.create(dto, userId).toDto();
    }

    @PatchMapping("{id}")
    public ItemDto change(@RequestBody ItemDto dto, @PathVariable Long id, @RequestHeader(userHeaderName) Long userId) {
        return service.patch(dto.toItem(null), id, userId).toDto();
    }

    @GetMapping("search")
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return service.search(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentInputDto dto, @PathVariable Long itemId, @RequestHeader(userHeaderName) Long userId) {
        return service.createComment(dto, itemId, userId).toDto();
    }
}

