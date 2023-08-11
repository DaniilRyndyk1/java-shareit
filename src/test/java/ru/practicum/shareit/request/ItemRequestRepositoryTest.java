package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
public class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Long userId = 1L;

    private User user;
    private User user2;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User(userId, "Nik", "nik@gmail.com");
        user2 = new User(userId + 1, "Danila", "danila@gmail.com");
        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        userId += 2;

        request = new ItemRequest(1L, "need..", user2, LocalDateTime.now(), null);
        request = itemRequestRepository.save(request);
    }

    @Test
    void shouldGetItemRequestsByRequestor() {
        var requests = itemRequestRepository.findAllByRequestor_idOrderByCreatedDesc(user2.getId());
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
    }

    @Test
    void shouldGetItemRequestsByOtherUser() {
        var pageRequest = PageRequest.of(0, 20);
        var requests = itemRequestRepository.findAllByRequestor_idNotOrderByCreatedDesc(user.getId(), pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
    }
}
