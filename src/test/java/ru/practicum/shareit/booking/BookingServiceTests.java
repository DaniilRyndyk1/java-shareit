package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.State;
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
        var item1 = itemService.create(itemDto, user1.getId());
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
}
