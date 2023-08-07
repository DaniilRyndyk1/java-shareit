package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTests {
    private final UserService userService;
    private final UserDto userDto = new UserDto(1L, "Danila", "konosuba@gmail.com");

    @Test
    void shouldCreateUser() {
        var newUser = userService.create(userDto);
        var newUser2 = userService.get(newUser.getId());
        assertEquals(newUser.getId(), newUser2.getId());
        assertEquals(newUser.getName(), newUser2.getName());
        assertEquals(newUser.getEmail(), newUser2.getEmail());
    }

    @Test
    void shouldNotCreateUserWithSameEmail() {
        userService.create(userDto);
        var users = userService.getAll();
        var originalSize = users.size();
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.create(new UserDto(2L, "Danila2", users.get(0).getEmail())));
        assertEquals(userService.getAll().size(), originalSize);
    }

    @Test
    void shouldNotCreateUserWithoutEmail() {
        var userDto = new UserDto(2L, "Danila2", null);
        assertThrows(DataIntegrityViolationException.class, () -> userService.create(userDto));
    }

    @Test
    void shouldUpdateUser() {
        var loadedUser = userService.create(userDto);
        var userDto = new UserDto(loadedUser.getId(), loadedUser.getName() + " - это я!", loadedUser.getEmail());
        userService.patch(userDto);
        var newUser = userService.get(userDto.getId());
        assertEquals(newUser.getName(), userDto.getName());
    }
}
