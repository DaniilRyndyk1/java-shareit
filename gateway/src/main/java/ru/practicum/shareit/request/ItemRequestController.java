package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Config;
import ru.practicum.shareit.group.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody ItemRequestDto dto, @RequestHeader(Config.userHeaderName) Long userId) {
        log.info("Create item request {}, userId={}", dto, userId);
        return client.create(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(Config.userHeaderName) Long userId) {
        log.info("Get items, userId={}", userId);
        return client.getAllByUser(userId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getByParams(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "1") Integer size, @RequestHeader(Config.userHeaderName) Long userId) {
        if (size <= 0 || from < 0) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("Get items, from={}, size={}, userId={}", from, size, userId);
        return client.getByParams(from, size, userId);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> get(@PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
        log.info("Get item {}, userId={}", id, userId);
        return client.get(id, userId);
    }
}
