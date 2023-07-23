package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.DatabaseItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ItemService {

    private final DatabaseItemRepository repository;
    private final UserService userService;

    public Item change(Item original, Item object) {
        if (object.getName() == null) {
            object.setName(original.getName());
        }
        if (object.getDescription() == null) {
            object.setDescription(original.getDescription());
        }
        if (object.getAvailable() == null) {
            object.setAvailable(original.getAvailable());
        }
        if (object.getOwner() == null) {
            object.setOwner(original.getOwner());
        }
        if (object.getRequest() == null) {
            object.setRequest(original.getRequest());
        }
        return object;
    }

    private void validateItem(Item item) {
        if (item.getName() == null) {
            throw new ValidationException("Имя не задано");
        } else if (item.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        } else if (item.getDescription() == null) {
            throw new ValidationException("Описание не задано");
        } else if (item.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        } else if (item.getAvailable() == null) {
            throw new ValidationException("Статус не может быть пустым");
        }
    }

    public Item get(@PathVariable long id) {
        var object = repository.findById(id);
        if (object.isEmpty()) {
            throw new NotFoundException(id, object.getClass().getSimpleName());
        }
        return object.get();
    }

    public List<Item> getAll() {
        return repository.findAll();
    }

    public void remove(long id) {
        var item = get(id);
        repository.delete(item);
    }

    public Item create(ItemDto object, long userId) {
        var item = object.toItem(null);
        validateItem(item);
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
       item.setOwner(user);
        return repository.save(item);
    }

    public Item patch(Item item, long id, long userId) {
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        var original = get(id);
        if (original == null) {
            throw new NotFoundException(id, "Предмет с таким id не существует");
        }
        if (userId != original.getOwner().getId()) {
            throw new NotFoundException(userId, "Пользователь не является владельцем");
        }
        item.setId(id);
        item = change(original, item);
        validateItem(item);
        return repository.save(item);
    }

    public List<ItemDto> getAll(Long userId) {
        var items = getAll();
        var result = new ArrayList<ItemDto>();
        for (Item item: items) {
            if (item.getOwner().getId().equals(userId)) {
                result.add(item.toDto());
            }
        }
        return result;
    }

    public List<ItemDto> search(String text) {
        text = text.toLowerCase();
        var items = getAll();
        var result = new ArrayList<ItemDto>();
        var pattern = Pattern.compile(text);
        for (var item : items) {
            if (item.getAvailable()) {
                var matcher = pattern.matcher(item.getName().toLowerCase());
                if (matcher.find()) {
                    result.add(item.toDto());
                    continue;
                }
                matcher = pattern.matcher(item.getDescription().toLowerCase());
                if (matcher.find()) {
                    result.add(item.toDto());
                }
            }
        }

        return result;
    }
}
