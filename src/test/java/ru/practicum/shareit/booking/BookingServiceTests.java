package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTests {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final UserDto userDto1 = new UserDto(1L,"Danila","konosuba@gmail.com");
    private final UserDto userDto2 = new UserDto(2L,"Danila2","konosuba2@gmail.com");
    private final UserDto userDto3 = new UserDto(2L,"Danila3","konosuba3@gmail.com");
    private final ItemDto itemDto = new ItemDto(1L, "Sword", "Very very heavy", true, null);

    @Test
    void shouldCreateBooking() {
        var ownerDto = userService.create(userDto1);
        var anotherUser = userService.create(userDto2);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(20),
                LocalDateTime.now().plusHours(22),
                newItemDto.getId());
        var newBooking = bookingService.create(bookingInputDto, anotherUser.getId());
        assertNotNull(bookingService.get(newBooking.getId(), anotherUser.getId()));
    }

    @Test
    void shouldNotCreateBookingWithEndCross() {
        var user1 = userService.create(new UserDto(-1L, "xxx", "xxx@hmmsl.ru"));
        var user2 = userService.create(new UserDto(-1L, "xxx2", "xxx2@hmmsl.ru"));
        var itemDto1 = itemService.create(itemDto, user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(19),
                LocalDateTime.now().plusHours(21),
                itemDto1.getId());
        assertThrows(NotFoundException.class, () -> bookingService.create(bookingInputDto, user2.getId()));
    }

    @Test
    void shouldCreateBookingWithStatusNull() {
        var ownerDto = userService.create(userDto1);
        var anotherUser = userService.create(userDto2);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                newItemDto.getId());
        var newBooking = bookingService.create(bookingInputDto, anotherUser.getId());
        assertNotNull(bookingService.get(newBooking.getId(), anotherUser.getId()));
    }

    @Test
    void shouldNotCreateBookingWithWrongUserId() {
        var ownerDto = userService.create(userDto1);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var inputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(6),
                newItemDto.getId());
        assertThrows(NotFoundException.class, () -> bookingService.create(inputDto, 999L));
    }

    @Test
    void shouldNotCreateBookingWithWrongItemId() {
        var anotherUserDto = userService.create(userDto2);
        var inputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(6),
                999L);
        assertThrows(NotFoundException.class, () -> bookingService.create(inputDto, anotherUserDto.getId()));
    }

    @Test
    void shouldNotCreateBookingWithEndInPast() {
        var ownerDto = userService.create(userDto1);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var anotherUserDto = userService.create(userDto2);
        var inputDto = new BookingInputDto(
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().minusHours(4),
                newItemDto.getId());
        assertThrows(javax.validation.ConstraintViolationException.class,
                () -> bookingService.create(inputDto, anotherUserDto.getId()));
    }

    @Test
    void shouldNotCreateBookingWithEndBeforeStart() {
        var ownerDto = userService.create(userDto1);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var anotherUserDto = userService.create(userDto2);
        var inputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(4),
                newItemDto.getId());
        assertThrows(ValidationException.class,
                () -> bookingService.create(inputDto, anotherUserDto.getId()));
    }

    @Test
    void shouldNotCreateBookingWithStartEqualEnd() {
        var ownerDto = userService.create(userDto1);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var anotherUserDto = userService.create(userDto2);
        var time = LocalDateTime.now().plusHours(4);
        var inputDto = new BookingInputDto(
                time,
                time,
                newItemDto.getId());
        assertThrows(ValidationException.class,
                () -> bookingService.create(inputDto, anotherUserDto.getId()));
    }

    @Test
    void shouldNotCreateBookingWithStartEqualNull() {
        var ownerDto = userService.create(userDto1);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var anotherUserDto = userService.create(userDto2);
        var inputDto = new BookingInputDto(
                null,
                LocalDateTime.now().plusHours(4),
                newItemDto.getId());
        assertThrows(NullPointerException.class,
                () -> bookingService.create(inputDto, anotherUserDto.getId()));
    }

    @Test
    void shouldNotCreateBookingWithEndEqualNull() {
        var ownerDto = userService.create(userDto1);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var anotherUserDto = userService.create(userDto2);
        var inputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(4),
                null,
                newItemDto.getId());
        assertThrows(NullPointerException.class,
                () -> bookingService.create(inputDto, anotherUserDto.getId()));
    }

    @Test
    void shouldNotCreateBookingWithStartInPast() {
        var ownerDto = userService.create(userDto1);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var anotherUserDto = userService.create(userDto2);
        var inputDto = new BookingInputDto(
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().plusHours(4),
                newItemDto.getId());
        assertThrows(javax.validation.ConstraintViolationException.class,
                () -> bookingService.create(inputDto, anotherUserDto.getId()));
    }

    @Test
    void shouldNotCreateBookingByOwner() {
        var ownerDto = userService.create(userDto1);
        var newItemDto = itemService.create(itemDto, ownerDto.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(6),
                LocalDateTime.now().plusHours(7),
                newItemDto.getId());
        var exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto, ownerDto.getId()));
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
        var user = userService.create(userDto1);

        assertThrows(NullPointerException.class,
                () -> bookingService.getBookingsByOwnerAndState(null, user.getId(), 1, 1));
    }

    @Test
    void shouldNotGetBookingWithWrongId() {
        var user = userService.create(userDto1);

        assertThrows(NotFoundException.class,
                () -> bookingService.get(999L, user.getId()));
    }

    @Test
    void shouldNotApproveBookingByOtherUser() {
        var user1 = userService.create(userDto1);
        var item1 = itemService.create(itemDto, user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(9),
                item1.getId());
        var user2 = userService.create(userDto2);
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var user3 = userService.create(userDto3);

        assertThrows(RuntimeException.class,
                () -> bookingService.approve(booking.getId(), user3.getId(), false));
    }

    @Test
    void shouldNotApproveBookingByBooker() {
        var user1 = userService.create(userDto1);
        var item1 = itemService.create(itemDto, user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(11),
                LocalDateTime.now().plusHours(12),
                item1.getId());
        var user2 = userService.create(userDto2);
        var booking = bookingService.create(bookingInputDto, user2.getId());

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(booking.getId(), user2.getId(), false));
    }

    @Test
    void shouldNotApproveBookingAfterApprove() {
        var user1 = userService.create(userDto1);
        var item1 = itemService.create(new ItemDto(-1L, "test12", "test12", true, null), user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item1.getId());
        var user2 = userService.create(userDto2);
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user1.getId(), false);

        assertThrows(ValidationException.class,
                () -> bookingService.approve(booking.getId(), user1.getId(), true));
    }

    @Test
    void shouldApproveBooking() {
        var user1 = userService.create(userDto1);
        var item1 = itemService.create(new ItemDto(-1L, "baza2", "baza2", true, null), user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item1.getId());
        var user2 = userService.create(userDto2);
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user1.getId(), true);
        booking = bookingService.get(booking.getId(), user1.getId());
        assertEquals(booking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void shouldRejectedBooking() {
        var user1 = userService.create(userDto1);
        var item1 = itemService.create(new ItemDto(-1L, "baza", "baza", true, null), user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item1.getId());
        var user2 = userService.create(userDto2);
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user1.getId(), false);
        booking = bookingService.get(booking.getId(), user1.getId());
        assertEquals(booking.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void shouldNotApproveBookingByNotOwner() {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "test11", "test11", true, null), user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab34@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var user3 = userService.create(new UserDto(-1L, "aababab", "ababab4@hmmsl.ru"));

        assertThrows(RuntimeException.class,
                () -> bookingService.approve(booking.getId(), user3.getId(), false));
    }

    @Test
    void shouldNotGetBookingByNotOwnerAndNotBooker() {
        var user1 = userService.create(new UserDto(-1L, "gorsok", "gorsok@hmmsl.ru"));
        var item1 = itemService.create(itemDto, user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "gorsok2", "gorsok2@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var user3 = userService.create(new UserDto(-1L, "gorsok3", "gorsok3@hmmsl.ru"));

        assertThrows(NotFoundException.class,
                () -> bookingService.get(booking.getId(), user3.getId()));
    }

    @Test
    void shouldGetBookingByOwner() {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab655@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "artur", "artur", true, null), user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab374234@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());

        assertNotNull(bookingService.get(booking.getId(), user1.getId()));
    }

    @Test
    void shouldGetBookingByBooker() {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab6@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "nikita", "nikita", true, null), user1.getId());
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab374@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());

        assertNotNull(bookingService.get(booking.getId(), user2.getId()));
    }

    @Test
    void shouldNotGetAllBookingsByOwnerWithWrongSize() {
        var user = userService.create(new UserDto(-1L, "aababab", "abababs6555@hmmsl.ru"));
        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByOwnerAndState(State.CURRENT, user.getId(), 0, -1));
    }

    @Test
    void shouldNotGetAllBookingsByOwnerWithWrongFrom() {
        var user = userService.create(new UserDto(-1L, "aababab", "abababs6@hmmsl.ru"));
        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByOwnerAndState(State.CURRENT, user.getId(), -1, 20));
    }

    @Test
    void shouldGetBookingsByOwnerWithStateCurrent() throws InterruptedException {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab_1@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "test3", "test3", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusHours(100),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab_2@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());
        Thread.sleep(1500);
        var items = bookingService.getBookingsByOwnerAndState(State.CURRENT, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStatePast() throws InterruptedException {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab_3@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "test1", "test1", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab_4@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());
        Thread.sleep(3000);
        var items = bookingService.getBookingsByOwnerAndState(State.PAST, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStateFuture() {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab_5@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "test2", "test2", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab_6@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var items = bookingService.getBookingsByOwnerAndState(State.FUTURE, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStateWaiting() {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab_5_@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "test23", "test23", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab_6_@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());

        var items = bookingService.getBookingsByOwnerAndState(State.WAITING, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStateRejected() {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab_5_1@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "test231", "test231", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab_6_1@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());

        bookingService.approve(booking.getId(), user1.getId(), false);

        var items = bookingService.getBookingsByOwnerAndState(State.REJECTED, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByOwnerWithStateAll() {
        var user1 = userService.create(new UserDto(-1L, "aababab", "ababab_5_2@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "test2312", "test2312", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "aababab", "ababab_6_12@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());

        var items = bookingService.getBookingsByOwnerAndState(State.ALL, user1.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldNotGetAllBookingsByBookerWithWrongSize() {
        var user = userService.create(new UserDto(-1L, "aababab", "a@hmmsl.ru"));
        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByBookerAndState(State.CURRENT, user.getId(), 0, -1));
    }

    @Test
    void shouldNotGetAllBookingsByBookerWithWrongFrom() {
        var user = userService.create(new UserDto(-1L, "aababab", "ppppp@hmmsl.ru"));
        assertThrows(ValidationException.class,
                () -> bookingService.getBookingsByBookerAndState(State.CURRENT, user.getId(), -1, 20));
    }

    @Test
    void shouldGetBookingsByBookerWithStateCurrent() throws InterruptedException {
        var user1 = userService.create(new UserDto(-1L, "b", "b@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "a", "a", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusHours(100),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "b1", "b1@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());
        Thread.sleep(1500);
        var items = bookingService.getBookingsByBookerAndState(State.CURRENT, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStatePast() throws InterruptedException {
        var user1 = userService.create(new UserDto(-1L, "b3", "b4@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "a2", "a2", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "b4adfsdaf", "b4dasfsadf@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());
        Thread.sleep(3000);
        var items = bookingService.getBookingsByBookerAndState(State.PAST, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStateFuture() {
        var user1 = userService.create(new UserDto(-1L, "b5", "b5@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "a3", "test2", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "b6", "b6@mmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());
        var items = bookingService.getBookingsByBookerAndState(State.FUTURE, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStateWaiting() {
        var user1 = userService.create(new UserDto(-1L, "b7", "b7@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "a4", "test23", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "b8", "b8@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());

        var items = bookingService.getBookingsByBookerAndState(State.WAITING, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStateRejected() {
        var user1 = userService.create(new UserDto(-1L, "b9", "b9@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "a5", "test231", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "b10", "b10@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());

        bookingService.approve(booking.getId(), user1.getId(), false);

        var items = bookingService.getBookingsByBookerAndState(State.REJECTED, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldGetBookingsByBookerWithStateAll() {
        var user1 = userService.create(new UserDto(-1L, "b11", "b11@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "a6", "test2312", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "b12", "b12@hmmsl.ru"));
        var booking = bookingService.create(bookingInputDto, user2.getId());

        var items = bookingService.getBookingsByBookerAndState(State.ALL, user2.getId(), 0, 20);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), booking.getId());
    }

    @Test
    void shouldNotCreateBookingWithUnavailableItem() {
        var user1 = userService.create(new UserDto(-1L, "b11", "b111@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "a6", "test23121", false, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "b12", "b121@hmmsl.ru"));

        assertThrows(ValidationException.class,
                () -> bookingService.create(bookingInputDto, user2.getId()));
    }

    @Test
    void shouldNotCreateBookingIfThisTimeIsBusy() {
        var user1 = userService.create(new UserDto(-1L, "hello1", "hello1@hmmsl.ru"));
        var item1 = itemService.create(new ItemDto(-1L, "hello1123", "hello1123", true, null), user1.getId());

        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                item1.getId());
        var user2 = userService.create(new UserDto(-1L, "hello2", "hello2@hmmsl.ru"));
        bookingService.create(bookingInputDto, user2.getId());

        var user3 = userService.create(new UserDto(-1L, "b126", "b1216@hmmsl.ru"));
        var bookingInputDto2 = new BookingInputDto(
                LocalDateTime.now().plusHours(1).plusSeconds(1),
                LocalDateTime.now().plusHours(2).minusSeconds(1),
                item1.getId());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto2, user3.getId()));
    }
}
