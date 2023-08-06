package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInputDto;
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
        var inputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(4),
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
    void shouldNotCreateBookingWithEndEqualEnd() {
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
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                newItemDto.getId());
        var exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingInputDto, ownerDto.getId()));
        assertEquals("Владелец не может забронировать вещь", exception.getMessage());
    }

    @Test
    void shouldNotGetAllBookingsWithWrongUserId() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByOwnerAndState(State.CURRENT, 999L, 1, 1));
    }
}
