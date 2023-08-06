package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Config;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(@Validated(Create.class) @RequestBody ItemRequestInputDto dto, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(Config.userHeaderName) Long userId) {
        return service.getAllByUser(userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getByParams(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "1") Integer size, @RequestHeader(Config.userHeaderName) Long userId) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("Переданы неверные параметры");
        }
        return service.getAllByPage(from, size, userId);
    }

    @GetMapping("{id}")
    public ItemRequestDto get(@PathVariable Long id, @RequestHeader(Config.userHeaderName) Long userId) {
        return service.getDto(id, userId);
    }
}
