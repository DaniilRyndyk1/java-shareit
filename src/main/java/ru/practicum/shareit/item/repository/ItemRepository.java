package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_IdOrderById(Long ownerId);

    @Query(value = "SELECT * from item i where (lower(i.name) like %:text% or lower(i.description) like %:text%) AND i.available = True", nativeQuery = true)
    List<Item> findByText(String text);
}
