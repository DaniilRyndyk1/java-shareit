package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public User get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }

    public UserDto getDto(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден")));
    }

    public List<UserDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public void remove(Long id) {
        repository.delete(get(id));
    }

    public UserDto create(UserDto dto) {
        var user = mapper.toUser(dto);
        return mapper.toDto(repository.save(user));
    }

    public UserDto patch(UserDto dto) {
        var user = get(dto.getId());

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !user.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        return mapper.toDto(repository.save(user));
    }
}
