package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.base.repository.InMemoryRepository;
import ru.practicum.shareit.item.model.Item;

@Component
public class InMemoryItemRepository extends InMemoryRepository<Item> {
}
