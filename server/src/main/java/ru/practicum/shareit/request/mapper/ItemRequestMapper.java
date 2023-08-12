package ru.practicum.shareit.request.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemRequestMapper {
    public final ItemMapper itemMapper;

    public ItemRequestDto toDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                request.getItems().stream().map(itemMapper::toDto).collect(Collectors.toSet()));
    }

    public ItemRequest toRequest(ItemRequestDto dto, Long id, User requestor, LocalDateTime created, Set<Item> items) {
        return new ItemRequest(
                id,
                dto.getDescription(),
                requestor,
                created,
                items);
    }
}
