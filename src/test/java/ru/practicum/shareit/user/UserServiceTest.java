package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private UserDto user = new UserDto(0L, "Danila", "konosuba@gmail.com");

    @BeforeEach
    void setup() {
        user = userService.create(user);
    }

    @Test
    void shouldCreateUser() {
        var newUser = userService.create(new UserDto(0L, "Danila2", "konosuba2@gmail.com"));
        var newUser2 = userService.get(newUser.getId());
        assertEquals(newUser.getId(), newUser2.getId());
        assertEquals(newUser.getName(), newUser2.getName());
        assertEquals(newUser.getEmail(), newUser2.getEmail());
    }

    @Test
    void shouldNotCreateUserWithSameEmail() {
        assertEquals(1, userService.getAll().size());
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.create(new UserDto(0L, "Danila", user.getEmail())));
    }

    @Test
    void shouldNotCreateUserWithoutEmail() {
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.create(new UserDto(0L, "Danila2", null)));
    }

    @Test
    void shouldUpdateUser() {
        var newUserDto = new UserDto(user.getId(), user.getName() + " - это я!", user.getEmail());
        userService.patch(newUserDto);
        var newUser = userService.get(newUserDto.getId());
        assertEquals(newUser.getName(), newUserDto.getName());
    }

    @Test
    void shouldRemoveUser() {
        userService.remove(user.getId());
        assertEquals(0, userService.getAll().size());
    }

    @Test
    void shouldGetUserDto() {
        var dto = userService.getDto(user.getId());
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }
}
