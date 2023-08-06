package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTests {
    private final ItemService itemService;
    private final UserService userService;

    private final UserDto user1Dto = new UserDto(1L,"Danila","konosuba@gmail.com");
    private final UserDto user2Dto = new UserDto(1L,"Danila2","konosuba2@gmail.com");
    private final ItemDto itemDto = new ItemDto(1L, "Sword", "Very very heavy", true, null);
    private final ItemDto item2Dto = new ItemDto(1L, "Another Sword", "Not very heavy", true, null);

    @Test
    void shouldCreateItem() {
        var user = userService.create(user2Dto);
        var item3Dto = new ItemDto(-1L, "test", "test", true, null);
        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        itemService.create(item3Dto, user.getId());
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();

        assertEquals(newSize, originalSize + 1);
    }

    @Test
    void shouldNotCreateItemWithNotFoundedUser() {
        //var user = userService.create(user1Dto);

        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertThrows(NotFoundException.class,
                () -> itemService.create(item2Dto, 999L));
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldNotCreateItemWithoutAvailable() {
        //var user = userService.create(user1Dto);
        var newDto = new ItemDto(0L, "item", "same item", null, null);

        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(newDto, 1L));
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldNotCreateItemWithoutName() {
        //var user = userService.create(user1Dto);
        var newDto = new ItemDto(0L, null, "same item", true, null);

        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(newDto, 1L));
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldNotCreateItemWithoutDescription() {
        //var user = userService.create(user1Dto);
        var newDto = new ItemDto(0L, "item", null, true, null);

        var originalSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertThrows(DataIntegrityViolationException.class,
                () -> itemService.create(newDto, 1L));
        var newSize = itemService.getAllWithBookings(0,10, 1L).size();
        assertEquals(newSize, originalSize);
    }

    @Test
    void shouldUpdateItem() {
        userService.create(user1Dto);
        itemService.create(itemDto, 1L);

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
}
