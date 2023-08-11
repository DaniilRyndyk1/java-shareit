package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private UserDto user = new UserDto(1L,"Danila","konosuba@gmail.com");
    private UserDto user2 = new UserDto(2L,"Danila2","konosuba2@gmail.com");
    private final ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto("I NEED A SWORD");

    @BeforeEach
    void setup() {
        user = userService.create(user);
    }

    @Test
    void shouldCreateItemRequest() {
        assertNotNull(itemRequestService.create(itemRequestInputDto, user.getId()));
    }

    @Test
    void shouldNotCreateItemRequestWithWrongUser() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(itemRequestInputDto, 999L));
    }

    @Test
    void shouldNotCreateNullItemRequest() {
        assertThrows(DataIntegrityViolationException.class,
                () -> itemRequestService.create(new ItemRequestInputDto(), user.getId()));
    }

    @Test
    void shouldNotGetItemRequestWithWrongUser() {
        var request = itemRequestService.create(itemRequestInputDto, user.getId());
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getDto(request.getId(), 999L));
    }

    @Test
    void shouldNotGetItemRequestWithWrongId() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getDto(999L, user.getId()));
    }

    @Test
    void shouldNotGetAllRequestsWithFrom0AndSize0() {
        assertThrows(ValidationException.class,
                () -> itemRequestService.getAllByPage(0, 0, user.getId()));
    }

    @Test
    void shouldNotGetAllRequestsWithNegativeFromAndSize20() {
        assertThrows(ValidationException.class,
                () -> itemRequestService.getAllByPage(-1, 20, user.getId()));
    }

    @Test
    void shouldNotGetAllRequestsWithFrom0AndNegativeSize() {
        assertThrows(ValidationException.class,
                () -> itemRequestService.getAllByPage(0, -1, user.getId()));
    }

    @Test
    void shouldGetEmptyListWithFrom0AndNegativeSize() {
        assertEquals(0, itemRequestService.getAllByUser(user.getId()).size());
    }

    @Test
    void shouldGetAllItemRequestsByUser() {
        user2 = userService.create(user2);
        var itemRequest = itemRequestService.create(itemRequestInputDto, user.getId());
        var requests = itemRequestService.getAllByPage(0, 20, user2.getId());
        assertEquals(requests.size(), 1);
        var first = requests.get(0);
        assertEquals(first.getId(), itemRequest.getId());
        assertEquals(first.getDescription(), itemRequest.getDescription());
        assertEquals(first.getCreated(), itemRequest.getCreated());
        assertEquals(first.getItems().size(), itemRequest.getItems().size());
    }
}
