package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.base.repository.InMemoryRepository;
import ru.practicum.shareit.user.model.User;

@Component
public class InMemoryUserRepository extends InMemoryRepository<User> {
}
