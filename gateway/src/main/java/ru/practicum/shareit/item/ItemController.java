package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Config;
import ru.practicum.shareit.group.Create;
import ru.practicum.shareit.group.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInputDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient client;

    @GetMapping("{id}")
    public ResponseEntity<Object> get(@PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
        log.info("Get item {}, userId={}", id, userId);
        return client.get(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestHeader(Config.userHeaderName) Long userId) {
        if (size <= 0 || from < 0) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Get items, from={}, size={}, userId={}", from, size, userId);
        return client.getAll(from, size, userId);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Delete item {}", id);
        return client.delete(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody ItemInputDto dto, @RequestHeader(Config.userHeaderName) Long userId) {
        log.info("Create item {}, userId={}", dto, userId);
        return client.create(dto, userId);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> change(@Validated(Update.class) @RequestBody ItemInputDto dto, @PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
        log.info("Change item {} with id {}, userId={}", dto, id, userId);
        return client.change(dto, id, userId);
    }

    @GetMapping("search")
    public ResponseEntity<Object> search(String text, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        if (size <= 0 || from < 0) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Search items by text={}, from={}, size={}", text, from, size);
        return client.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@Validated(Create.class) @RequestBody CommentDto dto, @PathVariable Long itemId, @RequestHeader(Config.userHeaderName) Long userId) {
        log.info("Create comment {}, itemId={}, userId={}", dto, itemId, userId);
        return client.createComment(dto, itemId, userId);
    }
}
