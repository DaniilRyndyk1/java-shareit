package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;
    private UserDto user = new UserDto(1L,"Danila","konosuba@gmail.com");
    private UserDto user2 = new UserDto(3L,"Danila3","konosuba3@gmail.com");
    private ItemDto item = new ItemDto(1L, "Sword", "Very very heavy", true, null);
    private ItemDto item2 = new ItemDto(2L, "Another Sword", "Not very heavy", true, null);
    private ItemDto item3 = new ItemDto(3L, "test", "test", true, null);
    private final CommentInputDto commentInput = new CommentInputDto("Круткой комент (кто не согласен - его проблемы)");

    @BeforeEach
    void setup() {
        user = userService.create(user);
        user2 = userService.create(user2);
        item = itemService.create(item, user.getId());
        item2 = itemService.create(item2, user2.getId());
    }

    @Test
    void shouldCreateItem() {
        var originalSize = itemService.getAllWithBookings(0,10, user.getId()).size();
        item3 = itemService.create(item3, user.getId());
        var newSize = itemService.getAllWithBookings(0,10, user.getId()).size();
        assertEquals(newSize, originalSize + 1);
    }

    @Test
    void shouldNotCreateItemWithNotFoundedUser() {
        var originalSize = itemService.getAllWithBookings(0,10, user.getId()).size();
        assertThrows(NotFoundException.class,
                () -> itemService.create(item3, 999L));
        var newSize = itemService.getAllWithBookings(0,10, user.getId()).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldNotCreateItemWithoutAvailable() {
        var item4 = new ItemDto(-1L, "item", "same item", null, null);
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(item4, user.getId()));
    }

    @Test
    void shouldNotCreateItemWithoutName() {
        var item4 = new ItemDto(0L, null, "same item", true, null);
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(item4, user.getId()));
    }

    @Test
    void shouldNotCreateItemWithoutDescription() {
        var item5 = new ItemDto(-1L, "item", null, true, null);
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(item5, user2.getId()));
    }

    @Test
    void shouldUpdateItem() {
        var item3 = new ItemDto(item.getId(), item.getName() + "Hello!!", item.getDescription(), true, null);
        item3 = itemService.patch(item3, item.getId(), user.getId());
        assertNotEquals(item3.getName(), item.getName());
        assertEquals(item3.getDescription(), item.getDescription());
        assertEquals(item3.getAvailable(), item.getAvailable());
        assertNull(item3.getRequestId());
    }

    @Test
    void shouldSearchItemByName() {
        assertEquals(itemService.search("another", 0, 1).size(), 1);
    }

    @Test
    void shouldSearchItemByDescription() {
        assertEquals(itemService.search("Not", 0, 1).size(), 1);
    }

    @Test
    void shouldCreateComment() throws InterruptedException {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                item2.getId());
        bookingService.create(bookingInputDto, user.getId());
        Thread.sleep(2000);
        itemService.createComment(commentInput, item2.getId(), user.getId());
        var itemNewDto = itemService.getWithBookings(item2.getId(), user.getId());
        assertEquals(1, itemNewDto.getComments().size());
    }

    @Test
    void shouldNotCreateCommentWithNotEndedBooking() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(11),
                LocalDateTime.now().plusHours(12),
                item.getId());
        bookingService.create(bookingInputDto, user2.getId());
        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentInput, item.getId(), user2.getId()));
    }

    @Test
    void shouldNotCreateCommentWithRejectedBooking() {
        var bookingInputDto = new BookingInputDto(
                LocalDateTime.now().plusHours(11),
                LocalDateTime.now().plusHours(12),
                item.getId());
        var booking = bookingService.create(bookingInputDto, user2.getId());
        bookingService.approve(booking.getId(), user.getId(), false);
        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentInput, item.getId(), user2.getId()));
    }

    @Test
    void shouldNotCreateNullComment() {
        assertThrows(NotFoundException.class,
                () -> itemService.createComment(new CommentInputDto(null), item2.getId(), 2L));
    }

    @Test
    void shouldNotSearchWithEmptyText() {
        assertEquals(0, itemService.search("", 0, 20).size());
    }

    @Test
    void shouldNotSearchWithWrongParams() {
        assertThrows(ValidationException.class,
                () -> itemService.search("Hello!!", -1, 20));
    }

    @Test
    void shouldNotPatchByNoOwnerAndNoBooker() {
        var user3 = userService.create(new UserDto(-1L, "a", "a@gmail.com"));
        var dto = new ItemDto(-1L, "b", "b", true, null);
        var item3 = itemService.create(dto, user.getId());
        assertThrows(NotFoundException.class, () -> itemService.patch(dto, item3.getId(), user3.getId()));
    }

    @Test
    void shouldGetItemInfoWithBooking() {
        var start = LocalDateTime.now().plusHours(1);
        var end = LocalDateTime.now().plusHours(2);
        var booking = bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        var itemInfo = itemService.getWithBookings(item.getId(), user.getId());
        assertEquals(itemInfo.getNextBooking().getId(), booking.getId());
    }

    @Test
    void shouldGetItemInfoWithCurrentBooking() throws InterruptedException {
        var start = LocalDateTime.now().plusSeconds(1);
        var end = LocalDateTime.now().plusSeconds(20);
        var booking = bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        Thread.sleep(2000);
        var itemInfo = itemService.getWithBookings(item.getId(), user.getId());
        assertEquals(itemInfo.getCurrentBooking().getId(), booking.getId());
    }

    @Test
    void shouldNotCreateCommentToItemWithoutBookings() {
        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentInput, item.getId(), user2.getId()));
    }

    @Test
    void shouldCreateItemByRequest() {
        var itemRequest = itemRequestService.create(new ItemRequestInputDto("Хочу чего-то вкусного.."), user.getId());
        var originalSize = itemService.getAllWithBookings(0, 50, user2.getId()).size();
        var item3 = new ItemDto(-1L, "b22_", "b22_", true, itemRequest.getId());
        item3 = itemService.create(item3, user.getId());
        var items = itemService.getAllWithBookings(0, 50, user.getId());
        var newSize = items.size();
        assertEquals(newSize, originalSize + 1);
        assertEquals(item3.getId(), items.get(1).getId());
    }

    @Test
    void shouldRemoveItem() {
        var originalSize = itemService.getAllWithBookings(0, 20, user.getId()).size();
        itemService.remove(item.getId());
        var newSize = itemService.getAllWithBookings(0, 20, user.getId()).size();
        assertEquals(newSize, originalSize - 1);
    }

    @Test
    void shouldGetAllItemsWithComments() throws InterruptedException {
        var start = LocalDateTime.now().plusSeconds(1);
        var end = LocalDateTime.now().plusSeconds(2);
        bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        Thread.sleep(2000);
        var comment = itemService.createComment(commentInput, item.getId(), user2.getId());
        var items = itemService.getAllWithBookings(0, 20, user.getId());
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getComments().size(), 1);
        assertEquals(items.get(0).getComments().get(0).getId(), comment.getId());
    }

    @Test
    void shouldGetAllItemsWithBookings() throws InterruptedException {
        var start = LocalDateTime.now().plusSeconds(1);
        var end = LocalDateTime.now().plusSeconds(200);
        var booking2 = bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        Thread.sleep(2000);

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        var booking3 = bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());

        var items = itemService.getAllWithBookings(0, 20, user.getId());
        assertEquals(items.size(), 1);
        assertNotNull(items.get(0).getCurrentBooking());
        assertEquals(items.get(0).getCurrentBooking().getId(), booking2.getId());

        assertNotNull(items.get(0).getNextBooking());
        assertEquals(items.get(0).getNextBooking().getId(), booking3.getId());

        assertNotNull(items.get(0).getLastBooking());
        assertEquals(items.get(0).getLastBooking().getId(), booking2.getId());
    }

    @Test
    void shouldNotGetAllWithWrongSize() throws InterruptedException {
        var start = LocalDateTime.now().plusSeconds(1);
        var end = LocalDateTime.now().plusSeconds(2);
        bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        Thread.sleep(2000);
        itemService.createComment(commentInput, item.getId(), user2.getId());
        assertThrows(ValidationException.class, () -> itemService.getAllWithBookings(0, -1, user.getId()));
    }

    @Test
    void shouldNotGetAllWithWrongFrom() throws InterruptedException {
        var start = LocalDateTime.now().plusSeconds(1);
        var end = LocalDateTime.now().plusSeconds(2);
        bookingService.create(new BookingInputDto(start, end, item.getId()), user2.getId());
        Thread.sleep(2000);
        itemService.createComment(commentInput, item.getId(), user2.getId());
        assertThrows(ValidationException.class, () -> itemService.getAllWithBookings(-1, 20, user.getId()));
    }
}
