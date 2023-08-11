package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
public class CommentRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Long userId = 1L;
    private Long itemId = 1L;
    private Long bookingId = 1L;

    private Item item;
    private Comment comment;

    @BeforeEach
    void setUp() throws InterruptedException {
        var user = new User(userId, "Nik", "nik@gmail.com");
        var user2 = new User(userId + 1, "Danila", "danila@gmail.com");
        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        userId += 2;

        item = new Item(itemId, "Test item", "TEST!", true, user, null);
        item = itemRepository.save(item);
        itemId++;

        LocalDateTime now = LocalDateTime.now();
        var booking = new Booking(bookingId + 1, now.plusSeconds(1), now.plusSeconds(2), item, user2, BookingStatus.APPROVED);
        bookingRepository.save(booking);
        bookingId += 1;

        Thread.sleep(2500);
        comment = new Comment(1L, "Cool", item, user2, LocalDateTime.now());
        comment = commentRepository.save(comment);
    }

    @Test
    void shouldGetCommentsByItem() {
        var comments = commentRepository.findAllByItem_Id(item.getId());
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
    }

    @Test
    void shouldGetCommentByItemInList() {
        var items = List.of(item);
        var comments = commentRepository.findByItemIn(items);
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
    }
}
