package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTests {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;
    private final UserDto user1Dto = new UserDto(1L,"Danila","konosuba@gmail.com");
    private final UserDto user2Dto = new UserDto(2L,"Danila2","konosuba2@gmail.com");
    private final ItemDto itemDto = new ItemDto(1L, "Sword", "Very very heavy", true, null);
    private final ItemDto item2Dto = new ItemDto(2L, "Another Sword", "Not very heavy", true, null);
    private final ItemRequestInputDto itemRequestDto = new ItemRequestInputDto("I NEED A SWORD");

    @Test
    void shouldCreateItemRequest() {
        var user = userService.create(user1Dto);
        assertNotNull(itemRequestService.create(itemRequestDto, user.getId()));
    }

    @Test
    void shouldNotCreateItemRequestWithWrongUser() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, 999L));
    }

    @Test
    void shouldNotCreateNullItemRequest() {
        assertThrows(DataIntegrityViolationException.class,
                () -> itemRequestService.create(new ItemRequestInputDto(), 1L));
    }

    @Test
    void shouldNotGetItemRequestWithWrongUser() {
        var user = userService.create(user1Dto);
        var request = itemRequestService.create(new ItemRequestInputDto("test"), user.getId());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getDto(request.getId(), 999L));
    }

    @Test
    void shouldNotGetItemRequestWithWrongId() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getDto(999L, 1L));
    }

    @Test
    void shouldNotGetAllRequestsWithFrom0AndSize0() {
        var user = userService.create(user1Dto);
        assertThrows(ValidationException.class,
                () -> itemRequestService.getAllByPage(0, 0, user.getId()));
    }

    @Test
    void shouldNotGetAllRequestsWithNegativeFromAndSize20() {
        var user = userService.create(user1Dto);
        assertThrows(ValidationException.class,
                () -> itemRequestService.getAllByPage(-1, 20, user.getId()));
    }

    @Test
    void shouldNotGetAllRequestsWithFrom0AndNegativeSize() {
        var user = userService.create(user1Dto);
        assertThrows(ValidationException.class,
                () -> itemRequestService.getAllByPage(0, -1, user.getId()));
    }

    @Test
    void shouldGetEmptyListWithFrom0AndNegativeSize() {
        var user = userService.create(user1Dto);
        assertEquals(0, itemRequestService.getAllByUser(user.getId()).size());
    }

    @Test
    void shouldGetAllItemRequestsByUser() {
        var user = userService.create(new UserDto(-1L, "sdfads", "sdfasfd@ya.ru"));
        var itemRequest = itemRequestService.create(itemRequestDto, user.getId());
        var requests = itemRequestService.getAllByPage(0, 20, user.getId() - 1);
        assertEquals(requests.size(), 2);
        var first = requests.get(0);
        assertEquals(first.getId(), itemRequest.getId());
        assertEquals(first.getDescription(), itemRequest.getDescription());
        assertEquals(first.getCreated(), itemRequest.getCreated());
        assertEquals(first.getItems().size(), itemRequest.getItems().size());
    }
}
