package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public Item change(Item original, Item object) {
        if (object.getName() == null) {
            object.setName(original.getName());
        }
        if (object.getDescription() == null) {
            object.setDescription(original.getDescription());
        }
        if (object.getAvailable() == null) {
            object.setAvailable(original.getAvailable());
        }
        if (object.getOwner() == null) {
            object.setOwner(original.getOwner());
        }
        if (object.getRequest() == null) {
            object.setRequest(original.getRequest());
        }
        return object;
    }

    private void validateItem(Item item) {
        if (item.getName() == null) {
            throw new ValidationException("Имя не задано");
        } else if (item.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        } else if (item.getDescription() == null) {
            throw new ValidationException("Описание не задано");
        } else if (item.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        } else if (item.getAvailable() == null) {
            throw new ValidationException("Статус не может быть пустым");
        }
    }

    public Item get(@PathVariable long id) {
        var object = itemRepository.findById(id);
        if (object.isEmpty()) {
            throw new NotFoundException(id, object.getClass().getSimpleName());
        }

        return object.get();
    }

    public ItemDtoWithBooking getWithBookings(long id, long userId) {
        var object = itemRepository.findById(id);
        if (object.isEmpty()) {
            throw new NotFoundException(id, "Предмет отсутствует");
        }
        var item = object.get();
        Booking current = null;
        Booking next = null;
        Booking last = null;
        if (item.getOwner().getId().equals(userId)) {
            current = itemRepository.findCurrentBookingByItem(item.getId());

            if (item.getId() == 2 && userId == 4) {
                var test = 1;
            }

            var bookings = itemRepository.findNextBookingByItem(item.getId());
            if (bookings.size() != 0) {
                next = bookings.get(0);
            }

            bookings = itemRepository.findLastBookingByItem(item.getId());
            if (bookings.size() != 0) {
                last = bookings.get(0);
            }
        }
        var itemDto = item.toDtoWithBookings(current, next, last);
        var comments = commentRepository.findAllByItem_Id(item.getId());
        var commentsDto = new ArrayList<CommentDto>();
        for (Comment comment : comments) {
            commentsDto.add(comment.toDto());
        }

        itemDto.setComments(commentsDto);

        return itemDto;
    }

    public List<ItemDtoWithBooking> getAllWithBookings(long userId) {
        var object = userService.get(userId);
        if (object == null) {
            throw new NotFoundException(userId, "Предмет не найден");
        }
        var items = itemRepository.findAllByOwner_IdOrderById(userId);
        var result = new ArrayList<ItemDtoWithBooking>();
        for (Item item : items) {
            var itemDto = this.getWithBookings(item.getId(), item.getOwner().getId());
            result.add(itemDto);
        }
        return result;
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public void remove(long id) {
        itemRepository.delete(get(id));
    }

    public Item create(ItemDto object, long userId) {
        var item = object.toItem(null);
        validateItem(item);
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        item.setOwner(user);
        return itemRepository.save(item);
    }

    public Comment createComment(CommentInputDto object, long itemId, long userId) {
        if (object.getText().isEmpty()) {
            throw new ValidationException("Текст отзыва пустой");
        }

        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        var item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException(userId, "Предмет с таким id не существует");
        }

        var bookings = itemRepository.findBookingsByItemAndUser(itemId, userId);

        if (bookings.size() == 0) {
            throw new ValidationException("У предмета не было бронирований");
        }

        Booking oldestBooking = null;

        for (Booking booking: bookings) {
            if (!booking.getStatus().equals(BookingStatus.REJECTED)) {
                oldestBooking = booking;
                break;
            }
        }

        if (oldestBooking == null) {
            throw new ValidationException("У вас не было бронирований с этим предметом");
        }

        var now = LocalDateTime.now();

        if (oldestBooking.getEnd().isAfter(now)) {
            throw new ValidationException("У вас нет завершенных бронирований с этим предметом");
        }

        var comment = object.toComment(0L, user, item.get());
        return commentRepository.save(comment);
    }

    public Item patch(Item item, long id, long userId) {
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        var original = get(id);
        if (original == null) {
            throw new NotFoundException(id, "Предмет с таким id не существует");
        }
        if (userId != original.getOwner().getId()) {
            throw new NotFoundException(userId, "Пользователь не является владельцем");
        }
        item.setId(id);
        item = change(original, item);
        validateItem(item);
        return itemRepository.save(item);
    }

    public List<ItemDto> search(String text) {
        text = text.toLowerCase();
        var items = getAll();
        var result = new ArrayList<ItemDto>();
        var pattern = Pattern.compile(text);
        for (var item : items) {
            if (item.getAvailable()) {
                var matcher = pattern.matcher(item.getName().toLowerCase());
                if (matcher.find()) {
                    result.add(item.toDto());
                    continue;
                }
                matcher = pattern.matcher(item.getDescription().toLowerCase());
                if (matcher.find()) {
                    result.add(item.toDto());
                }
            }
        }

        return result;
    }
}
