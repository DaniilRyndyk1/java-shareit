package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Long userId = 1L;
    private Long itemId = 1L;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(userId, "Nik", "nik@gmail.com");
        user = userRepository.save(user);
        userId++;

        item = new Item(itemId, "Test item", "TEST!", true, user, null);
        item = itemRepository.save(item);
        itemId++;
    }

    @Test
    void shouldFindItemsByOwner() {
        var pageRequest = PageRequest.of(0, 20);
        var items = itemRepository.findAllByOwner_IdOrderById(user.getId(), pageRequest);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void shouldFindByText() {
        var pageRequest = PageRequest.of(0, 20);
        var items = itemRepository.findByText("item", pageRequest);
        assertEquals(1, items.size());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
    }
}
