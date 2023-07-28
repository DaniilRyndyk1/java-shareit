package ru.practicum.shareit.base.repository;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.base.Model;

public interface Repository<T extends Model> {
    T add(T object);

    Optional<T> find(long id);

    boolean remove(long id);

    List<T> getAll();

    T change(T model);
}
