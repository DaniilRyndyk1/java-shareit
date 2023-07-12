package ru.practicum.shareit.base.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.practicum.shareit.base.Model;

public interface Repository<T extends Model> {
    T add(T object);

    Optional<T> find(long id);

    boolean remove(long id);

    void clear();

    List<T> getAll();

    T change(T model);

    String getUpdateData(T object);

    String getInsertData(T object);

    T getObject(SqlRowSet set);
}
