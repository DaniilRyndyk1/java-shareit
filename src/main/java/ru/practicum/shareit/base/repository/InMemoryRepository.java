package ru.practicum.shareit.base.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.base.Model;

import java.util.*;

@Slf4j
@Component
public class InMemoryRepository<T extends Model> implements Repository<T> {
    private final Map<Long, T> objects;
    private long id = 1;

    public InMemoryRepository() {
        objects = new HashMap<>();
    }

    @Override
    public T add(T object) {
        object.setId(id);
        objects.put(id, object);
        log.info("Успешно был добавлен {} с id = {}", object.getClass().getSimpleName(), id);
        id++;
        return object;
    }

    @Override
    public Optional<T> find(long id) {
        return Optional.ofNullable(objects.get(id));
    }

    @Override
    public boolean remove(long id) {
        if (objects.containsKey(id)) {
            objects.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(objects.values());
    }

    @Override
    public T change(T model) {
        long id = model.getId();
        remove(id);
        objects.put(id, model);
        log.info("Успешно был обновлен {} с id = {}", model.getClass().getSimpleName(), id);
        return model;
    }
}
