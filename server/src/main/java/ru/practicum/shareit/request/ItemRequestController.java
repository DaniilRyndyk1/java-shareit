package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.Config;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto dto, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(Config.userHeaderName) Long userId) {
        return service.getAllByUser(userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getByParams(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "1") Integer size, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.getAllByPage(from, size, userId);
    }

    @GetMapping("{id}")
    public ItemRequestDto get(@PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.getDto(id, userId);
    }
}
