package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
@AutoConfigureTestDatabase
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Long userId = 1L;
    private Long itemId = 1L;
    private Long bookingId = 1L;

    private User user;
    private User user2;
    private Item item;
    private Booking lastBooking;
    private Booking currentBooking;
    private Booking nextBooking;

    @BeforeEach
    void setUp() throws InterruptedException {
        user = new User(userId, "Nik", "nik@gmail.com");
        user2 = new User(userId + 1, "Danila", "danila@gmail.com");
        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        userId += 2;

        item = new Item(itemId, "Test item", "TEST!", true, user, null);
        item = itemRepository.save(item);
        itemId++;

        LocalDateTime now = LocalDateTime.now();
        lastBooking = new Booking(bookingId, now.plusSeconds(1), now.plusSeconds(2), item, user2, BookingStatus.APPROVED);
        currentBooking = new Booking(bookingId + 1, now.plusSeconds(1), now.plusHours(100), item, user2, BookingStatus.APPROVED);
        nextBooking = new Booking(bookingId + 2, now.plusHours(5), now.plusHours(6), item, user2, BookingStatus.APPROVED);
        lastBooking = bookingRepository.save(lastBooking);
        currentBooking = bookingRepository.save(currentBooking);
        nextBooking = bookingRepository.save(nextBooking);
        bookingId += 3;
        Thread.sleep(2500);
    }

    @Test
    void shouldFindCurrentBookingsByUser() {
        var currentBookings = bookingRepository.findCurrentBookingsByUser(user.getId());
        assertEquals(1, currentBookings.size());
        assertEquals(currentBooking.getId(), currentBookings.get(0).getId());
    }

    @Test
    void shouldFindLastBookingsByUser() {
        var lastBookings = bookingRepository.findLastBookingsByUser(user.getId());
        assertEquals(2, lastBookings.size());
        assertEquals(lastBooking.getId(), lastBookings.get(0).getId());
    }

    @Test
    void shouldFindNextBookingsByUser() {
        var nextBookings = bookingRepository.findNextBookingsByUser(user.getId());
        assertEquals(1, nextBookings.size());
        assertEquals(nextBooking.getId(), nextBookings.get(0).getId());
    }

    @Test
    void shouldFindCurrentBookingsByItem() {
        var foundCurrentBooking = bookingRepository.findCurrentByItem(item.getId());
        assertNotNull(foundCurrentBooking);
        assertEquals(currentBooking.getId(), foundCurrentBooking.getId());
    }

    @Test
    void shouldFindLastBookingsByItem() {
        var lastBookings = bookingRepository.findLastBookingsByItem(item.getId());
        assertEquals(2, lastBookings.size());
        assertEquals(lastBooking.getId(), lastBookings.get(0).getId());
    }

    @Test
    void shouldFindNextBookingsByItem() {
        var nextBookings = bookingRepository.findNextBookingsByItem(item.getId());
        assertEquals(1, nextBookings.size());
        assertEquals(nextBooking.getId(), nextBookings.get(0).getId());
    }

    @Test
    void shouldFindAllBookingsByItemAndUser() {
        var bookings = bookingRepository.findAllByItemAndUser(item.getId(), user2.getId());
        assertEquals(3, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByOwner() {
        var pageRequest = PageRequest.of(0, 20);
        var bookings = bookingRepository.findAllByItem_Owner_IdOrderByEndDesc(user.getId(), pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(3, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByOwnerAndStartBeforeAndEndAfterSomeValues() {
        var pageRequest = PageRequest.of(0, 20);
        var start = LocalDateTime.now();
        var end = LocalDateTime.now().plusSeconds(1);
        var bookings = bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(user.getId(), start, end, pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(1, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByBookerAndStartBeforeAndEndAfterSomeValues() {
        var pageRequest = PageRequest.of(0, 20);
        var start = LocalDateTime.now();
        var end = LocalDateTime.now().minusSeconds(10);
        var bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(user2.getId(), start, end, pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(2, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByOwnerAndEndLessThanEqualSomeValue() {
        var pageRequest = PageRequest.of(0, 20);
        var end = LocalDateTime.now().plusSeconds(100);
        var bookings = bookingRepository.findAllByItem_Owner_IdAndEndLessThanEqualOrderByStartDesc(user.getId(), end, pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(1, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByBookerAndEndLessThanEqualSomeValue() {
        var pageRequest = PageRequest.of(0, 20);
        var end = LocalDateTime.now().plusSeconds(100);
        var bookings = bookingRepository.findAllByBooker_IdAndEndLessThanEqualOrderByStartDesc(user2.getId(), end, pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(1, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByOwnerAndStartGreaterThanEqualSomeValue() {
        var pageRequest = PageRequest.of(0, 20);
        var end = LocalDateTime.now().plusSeconds(1);
        var bookings = bookingRepository.findAllByItem_Owner_IdAndStartGreaterThanEqualOrderByStartDesc(user.getId(), end, pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(1, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByBookerAndStartGreaterThanEqualSomeValue() {
        var pageRequest = PageRequest.of(0, 20);
        var end = LocalDateTime.now().minusSeconds(1);
        var bookings = bookingRepository.findAllByBooker_IdAndStartGreaterThanEqualOrderByStartDesc(user2.getId(), end, pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(1, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByOwnerAndStatus() {
        var pageRequest = PageRequest.of(0, 20);
        var bookings = bookingRepository.findAllByItem_Owner_IdAndStatusIs(user.getId(), BookingStatus.APPROVED, pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(3, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByBookerAndStatus() {
        var pageRequest = PageRequest.of(0, 20);
        var bookings = bookingRepository.findAllByBooker_IdAndStatusIs(user2.getId(), BookingStatus.APPROVED, pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(3, bookings.size());
    }

    @Test
    void shouldFindAllBookingsByBooker() {
        var pageRequest = PageRequest.of(0, 20);
        var bookings = bookingRepository.findAllByBooker_IdOrderByEndDesc(user2.getId(), pageRequest)
                .stream()
                .collect(Collectors.toList());
        assertEquals(3, bookings.size());
    }

    @Test
    void shouldFindBookingsByItemAndStartBeforeAndEndAfter() {
        var start = LocalDateTime.now();
        var end = LocalDateTime.now().minusSeconds(10);
        var booking = bookingRepository.findFirstByItem_IdAndStartBeforeAndEndAfter(item.getId(), start, end);
        assertNotNull(booking);
        assertEquals(lastBooking.getId(), booking.getId());
    }
}