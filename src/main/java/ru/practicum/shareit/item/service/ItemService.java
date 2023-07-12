package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.base.exception.NotFoundException;
import ru.practicum.shareit.base.exception.ValidationException;
import ru.practicum.shareit.base.repository.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@org.springframework.stereotype.Service
public class ItemService {

    private final Repository<Item> repository;

    @Autowired
    public ItemService(InMemoryItemRepository repository) {
        this.repository = repository;
    }

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

    public void validate(Item object) {
        if (object.getName() == null) {
            throw new ValidationException("Name не задан", object);
        } else if (object.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым", object);
        } else if (object.getDescription() == null) {
            throw new ValidationException("Description не задан", object);
        } else if (object.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым", object);
        } else if (object.getAvailable() == null) {
            throw new ValidationException("Статус не может быть пустым", object);
        }
    }

    public Item get(@PathVariable long id) {
        var object = repository.find(id);
        if (object.isEmpty()) {
            throw new NotFoundException(id, object.getClass().getSimpleName());
        }
        return object.get();
    }

    public List<Item> getAll() {
        return repository.getAll();
    }

    public void remove(long id) {
        repository.remove(id);
    }

    public Item create(@RequestBody Item object) {
        validate(object);
        return repository.add(object);
    }

    public Item patch(@RequestBody Item object) {
        var original = get(object.getId());
        object = change(original, object);
        validate(object);
        return repository.change(object);
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
