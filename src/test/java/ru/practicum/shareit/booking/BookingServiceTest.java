package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto user1 = new UserDto(1L,"Danila","konosuba@gmail.com");
    private UserDto user2 = new UserDto(2L,"Danila2","konosuba2@gmail.com");
    private UserDto user3 = new UserDto(2L,"Danila3","konosuba3@gmail.com");
    private ItemDto item = new ItemDto(1L, "Sword", "Very very heavy", true, null);

    @BeforeEach
    void setup() {
        user1 = userService.create(user1);
        item = itemService.create(item, user1.getId());
        user2 = userService.create(user2);
        user3 = userService.create(user3);
    }

    @Test
    void shouldCreateBooking() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(20),
                LocalDateTime.now().plusHours(22),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        assertNotNull(bookingService.get(booking.getId(), user2.getId()));
    }

    @Test
    void shouldNotCreateBookingWithEndCross() {
        var bookingInputDto1 = new BookingInputDto(
                LocalDateTime.now().plusHours(20),
                LocalDateTime.now().plusHours(22),
                item.getId());
        bookingService.create(bookingInputDto1, user2.getId());
        var bookingInputDto2 = new BookingInputDto(
                LocalDateTime.now().plusHours(19),
                LocalDateTime.now().plusHours(21),
                item.getId());
        assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto2, user2.getId()));
    }

    @Test
    void shouldCreateBookingWithStatusNull() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        assertNotNull(bookingService.get(booking.getId(), user2.getId()));
    }

    @Test
    void shouldNotCreateBookingWithWrongUserId() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(6),
                item.getId());
        assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto, 999L));
    }

    @Test
    void shouldNotCreateBookingWithWrongItemId() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(6),
                999L);
        assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingWithEndInPast() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().minusHours(4),
                item.getId());
        assertThrows(javax.validation.ConstraintViolationException.class,
                () -> bookingService.create(bookingInputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingWithEndBeforeStart() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(4),
                item.getId());
        assertThrows(ValidationException.class,
                () -> bookingService.create(bookingInputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingWithStartEqualEnd() {
        var time = LocalDateTime.now().plusHours(4);
        var inputDto = new BookingInputDto(
                time,
                time,
                item.getId());
        assertThrows(ValidationException.class,
                () -> bookingService.create(inputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingWithStartEqualNull() {
        var inputDto = new BookingInputDto(
                null,
                LocalDateTime.now().plusHours(4),
                item.getId());
        assertThrows(NullPointerException.class,
                () -> bookingService.create(inputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingWithEndEqualNull() {
        var inputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(4),
                null,
                item.getId());
        assertThrows(NullPointerException.class,
                () -> bookingService.create(inputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingWithStartInPast() {
        var inputDto = new BookingInputDto(
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().plusHours(4),
                item.getId());
        assertThrows(javax.validation.ConstraintViolationException.class,
                () -> bookingService.create(inputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingByOwner() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(6),
                LocalDateTime.now().plusHours(7),
                item.getId());
        var exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto, user1.getId()));
        assertEquals("Владелец не может забронировать вещь", exception.getMessage());
    }

    @Test
    void shouldNotGetAllBookingsWithWrongUserId() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByBookerAndState(State.CURRENT, 999L, 1, 1));
    }

    @Test
    void shouldNotGetAllBookingsWithWrongOwnerId() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByOwnerAndState(State.CURRENT, 999L, 1, 1));
    }

    @Test
    void shouldNotGetAllBookingsWithWrongState() {
        assertThrows(NullPointerException.class,
                () -> bookingService.getBookingsByOwnerAndState(null, user1.getId(), 1, 1));
    }

    @Test
    void shouldNotGetBookingWithWrongId() {
        assertThrows(NotFoundException.class,
                () -> bookingService.get(999L, user1.getId()));
    }

    @Test
    void shouldNotApproveBookingByOtherUser() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(9),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        assertThrows(RuntimeException.class,
                () -> bookingService.approve(booking.getId(), user2.getId(), false));
    }

    @Test
    void shouldNotApproveBookingByBooker() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(11),
                LocalDateTime.now().plusHours(12),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        assertThrows(NotFoundException.class,
                () -> bookingService.approve(booking.getId(), user2.getId(), false));
    }

    @Test
    void shouldNotApproveBookingAfterApprove() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user1.getId(), false);
        assertThrows(ValidationException.class,
                () -> bookingService.approve(booking.getId(), user1.getId(), true));
    }

    @Test
    void shouldApproveBooking() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user1.getId(), true);
        booking = bookingService.get(booking.getId(), user1.getId());
        assertEquals(booking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void shouldRejectedBooking() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user1.getId(), false);
        booking = bookingService.get(booking.getId(), user1.getId());
        assertEquals(booking.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void shouldNotApproveBookingByNotOwner() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        assertThrows(RuntimeException.class,
                () -> bookingService.approve(booking.getId(), user3.getId(), false));
    }

    @Test
    void shouldNotGetBookingByNotOwnerAndNotBooker() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        assertThrows(NotFoundException.class,
                () -> bookingService.get(booking.getId(), user3.getId()));
    }

    @Test
    void shouldGetBookingByOwner() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        assertNotNull(bookingService.get(booking.getId(), user1.getId()));
    }

    @Test
    void shouldGetBookingByBooker() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        assertNotNull(bookingService.get(booking.getId(), user2.getId()));
    }

    @Test
    void shouldNotGetAllBookingsByOwnerWithWrongSize() {
        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByOwnerAndState(State.CURRENT, user1.getId(), 0, -1));
    }

    @Test
    void shouldNotGetAllBookingsByOwnerWithWrongFrom() {
        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByOwnerAndState(State.CURRENT, user1.getId(), -1, 20));
    }

    @Test
    void shouldGetBookingsByOwnerWithStateCurrent() throws InterruptedException {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusHours(100),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        Thread.sleep(1500);
        var items = bookingService.getBookingsByOwnerAndState(State.CURRENT, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStatePast() throws InterruptedException {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        Thread.sleep(3000);
        var items = bookingService.getBookingsByOwnerAndState(State.PAST, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStateFuture() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var items = bookingService.getBookingsByOwnerAndState(State.FUTURE, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStateWaiting() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var items = bookingService.getBookingsByOwnerAndState(State.WAITING, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStateRejected() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user1.getId(), false);
        var items = bookingService.getBookingsByOwnerAndState(State.REJECTED, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStateAll() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var items = bookingService.getBookingsByOwnerAndState(State.ALL, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldNotGetAllBookingsByBookerWithWrongSize() {
        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByBookerAndState(State.CURRENT, user1.getId(), 0, -1));
    }

    @Test
    void shouldNotGetAllBookingsByBookerWithWrongFrom() {
        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByBookerAndState(State.CURRENT, user1.getId(), -1, 20));
    }

    @Test
    void shouldGetBookingsByBookerWithStateCurrent() throws InterruptedException {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusHours(100),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        Thread.sleep(1500);
        var items = bookingService.getBookingsByBookerAndState(State.CURRENT, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStatePast() throws InterruptedException {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        Thread.sleep(3000);
        var items = bookingService.getBookingsByBookerAndState(State.PAST, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStateFuture() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var items = bookingService.getBookingsByBookerAndState(State.FUTURE, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStateWaiting() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var items = bookingService.getBookingsByBookerAndState(State.WAITING, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStateRejected() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user1.getId(), false);
        var items = bookingService.getBookingsByBookerAndState(State.REJECTED, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStateAll() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var items = bookingService.getBookingsByBookerAndState(State.ALL, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldNotCreateBookingWithUnavailableItem() {
        itemService.patch(
                new ItemDto(item.getId(), item.getName(), item.getDescription(), false, null),
                item.getId(),
                user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        assertThrows(ValidationException.class,
                () -> bookingService.create(bookingInputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingIfThisTimeIsBusy() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item.getId());
        bookingService.create(bookingInputDto, user2.getId());
        var bookingInputDto2 = new BookingInputDto(
                LocalDateTime.now().plusHours(1).plusSeconds(1),
                LocalDateTime.now().plusHours(2).minusSeconds(1),
                item.getId());
        assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto2, user3.getId()));
    }
}
