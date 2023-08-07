package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTests {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final UserDto user1Dto = new UserDto(1L,"Danila","konosuba@gmail.com");
    private final UserDto user2Dto = new UserDto(3L,"Danila3","konosuba3@gmail.com");
    private final ItemDto itemDto = new ItemDto(1L, "Sword", "Very very heavy", true, null);
    private final ItemDto item2Dto = new ItemDto(2L, "Another Sword", "Not very heavy", true, null);
    private final CommentInputDto commentInputDto = new CommentInputDto("Круткой комент (кто не согласен - его проблемы)");

    @Test
    void shouldCreateItem() {
        var item3Dto = new ItemDto(3L, "test", "test", true, null);
        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        itemService.create(item3Dto, 1L);
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();

        assertEquals(newSize, originalSize + 1);
    }

    @Test
    void shouldNotCreateItemWithNotFoundedUser() {
        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertThrows(NotFoundException.class,
                () -> itemService.create(item2Dto, 999L));
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldNotCreateItemWithoutAvailable() {
        var newDto = new ItemDto(0L, "item", "same item", null, null);

        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(newDto, 1L));
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldNotCreateItemWithoutName() {
        var newDto = new ItemDto(0L, null, "same item", true, null);

        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(newDto, 1L));
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldNotCreateItemWithoutDescription() {
        var newDto = new ItemDto(0L, "item", null, true, null);

        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(newDto, 1L));
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldUpdateItem() {
        var original = itemService.get(1L);
        var newItem = new ItemDto(original.getId(), original.getName() + "Hello!!", original.getDescription(), true, null);
        var itemAfterPatch = itemService.patch(newItem, original.getId(), original.getOwner().getId());
        assertNotEquals(itemAfterPatch.getName(), original.getName());
        assertEquals(itemAfterPatch.getDescription(), original.getDescription());
        assertEquals(itemAfterPatch.getAvailable(), original.getAvailable());
        assertNull(itemAfterPatch.getRequestId());
    }

    @Test
    void shouldSearchItemByName() {
        assertEquals(itemService.search("Hello!!", 0, 1).size(), 1);
    }

    @Test
    void shouldSearchItemByDescription() {
        assertEquals(itemService.search("heavy", 0, 1).size(), 1);
    }

    @Test
    void shouldCreateComment() throws InterruptedException {
        var itemId = itemService.create(item2Dto, 1L).getId();
        //var user2 = userService.create(user3Dto);
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                itemId);
        bookingService.create(bookingInputDto, 2L);
        Thread.sleep(2000);
        itemService.createComment(commentInputDto, itemId, 2L);
        var itemNewDto = itemService.getWithBookings(itemId, 2L);
        assertEquals(1, itemNewDto.getComments().size());
    }

    @Test
    void shouldNotCreateCommentWithNotEndedBooking() {
        userService.create(user1Dto);
        itemService.create(itemDto, 1L);

        var itemId = itemService.create(item2Dto, 1L).getId();
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(11),
                LocalDateTime.now().plusHours(12),
                itemId);
        var user2 = userService.create(user2Dto);
        bookingService.create(bookingInputDto, user2.getId());
        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentInputDto, itemId, user2.getId()));
    }

    @Test
    void shouldNotCreateNullComment() {
        var itemId = itemService.create(item2Dto, 1L).getId();
        assertThrows(ValidationException.class,
                () -> itemService.createComment(new CommentInputDto(null), itemId, 2L));
    }
}
