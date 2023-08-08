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
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.service.ItemRequestService;
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
    private final ItemRequestService itemRequestService;
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

    @Test
    void shouldNotSearchWithEmptyText() {
        assertEquals(0, itemService.search("", 0, 20).size());
    }

    @Test
    void shouldNotSearchWithWrongParams() {
        assertThrows(ValidationException.class, () -> itemService.search("Hello!!", -1, 20));
    }

    @Test
    void shouldNotPatchByNoOwnerAndNoBooker() {
        var user = userService.create(new UserDto(-1L, "a", "a@gmail.com"));
        var dto = new ItemDto(-1L, "b", "b", true, null);
        var item = itemService.create(dto, user.getId());
        dto.setAvailable(false);
        var user2 = userService.create(new UserDto(-1L, "aa", "aa@gmail.com"));
        assertThrows(NotFoundException.class, () -> itemService.patch(dto, item.getId(), user2.getId()));
    }

    @Test
    void shouldGetItemInfoWithBooking() {
        var user = userService.create(new UserDto(-1L, "a2", "a2@gmail.com"));
        var item = itemService.create(new ItemDto(-1L, "b2", "b2", true, null), user.getId());
        var user2 = userService.create(new UserDto(-1L, "aa3", "aa3@gmail.com"));
        var start = LocalDateTime.now().plusHours(1);
        var end = LocalDateTime.now().plusHours(2);
        var booking = bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        var itemInfo = itemService.getWithBookings(item.getId(), user.getId());
        assertEquals(itemInfo.getNextBooking().getId(), booking.getId());
    }

    @Test
    void shouldNotCreateCommentToItemWithoutBookings() {
        var user = userService.create(new UserDto(-1L, "a21", "a21@gmail.com"));
        var item = itemService.create(new ItemDto(-1L, "b21", "b21", true, null), user.getId());
        var user2 = userService.create(new UserDto(-1L, "aa31", "aa31@gmail.com"));
        assertThrows(ValidationException.class, () -> itemService.createComment(commentInputDto, item.getId(), user2.getId()));
    }

    @Test
    void shouldCreateItemByRequest() {
        var user = userService.create(new UserDto(-1L, "a22_", "a22_@gmail.com"));
        var itemRequest = itemRequestService.create(new ItemRequestInputDto("Хочу чего-то вкусного.."), user.getId());
        var user2 = userService.create(new UserDto(-1L, "aa32_", "aa32_@gmail.com"));
        var originalSize = itemService.getAllWithBookings(0, 50, user2.getId()).size();
        var item = itemService.create(new ItemDto(-1L, "b22_", "b22_", true, itemRequest.getId()), user.getId());
        var items = itemService.getAllWithBookings(0, 50, user.getId());
        var newSize = items.size();
        assertEquals(newSize, originalSize + 1);
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void shouldRemoveItem() {
        var user = userService.create(new UserDto(-1L, "a22", "a22@gmail.com"));
        var item = itemService.create(new ItemDto(-1L, "b22", "b22", true, null), user.getId());
        var originalSize = itemService.getAllWithBookings(0, 20, user.getId()).size();
        itemService.remove(item.getId());
        var newSize = itemService.getAllWithBookings(0, 20, user.getId()).size();
        assertEquals(newSize, originalSize - 1);
    }

    @Test
    void shouldGetAllItemsWithComments() throws InterruptedException {
        var user = userService.create(new UserDto(-1L, "a2s", "a2s@gmail.com"));
        var item = itemService.create(new ItemDto(-1L, "b2s", "b2s", true, null), user.getId());
        var user2 = userService.create(new UserDto(-1L, "aa3s", "aa3s@gmail.com"));
        var start = LocalDateTime.now().plusSeconds(1);
        var end = LocalDateTime.now().plusSeconds(2);
        bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        Thread.sleep(2000);
        var comment = itemService.createComment(commentInputDto, item.getId(), user2.getId());
        var items = itemService.getAllWithBookings(0, 20, user.getId());
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getComments().size(), 1);
        assertEquals(items.get(0).getComments().get(0).getId(), comment.getId());
    }

    @Test
    void shouldNotGetAllWithWrongParams() throws InterruptedException {
        var user = userService.create(new UserDto(-1L, "a2ss", "a2ss@gmail.com"));
        var item = itemService.create(new ItemDto(-1L, "b2ss", "b2ss", true, null), user.getId());
        var user2 = userService.create(new UserDto(-1L, "aa3ss", "aa3ss@gmail.com"));
        var start = LocalDateTime.now().plusSeconds(1);
        var end = LocalDateTime.now().plusSeconds(2);
        bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        Thread.sleep(2000);
        itemService.createComment(commentInputDto, item.getId(), user2.getId());
        assertThrows(ValidationException.class, () -> itemService.getAllWithBookings(0, -1, user.getId()));
    }
}
