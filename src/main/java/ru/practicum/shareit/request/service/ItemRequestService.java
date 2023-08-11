package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRequestMapper mapper;

    public ItemRequest get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Запрос с таким id не существует"));
    }

    public ItemRequestDto getDto(Long id, Long userId) {
        userService.get(userId);
        return mapper.toDto(get(id));
    }

    public ItemRequestDto create(ItemRequestInputDto inputDto, Long userId) {
        var user = userService.get(userId);
        var request = mapper.toRequest(inputDto, -1L, user, LocalDateTime.now(), new HashSet<>());
        return mapper.toDto(repository.save(request));
    }

    public List<ItemRequestDto> getAllByUser(Long userId) {
        userService.get(userId);
        return repository.findAllByRequestor_idOrderByCreatedDesc(userId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllByPage(int from, int size, Long userId) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("Переданы неверные параметры");
        }
        userService.get(userId);
        return repository.findAllByRequestor_idNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size)).stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
