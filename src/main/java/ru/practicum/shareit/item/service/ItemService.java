package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public Item change(Item original, Item item) {
        if (item.getName() == null) {
            item.setName(original.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(original.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(original.getAvailable());
        }
        if (item.getOwner() == null) {
            item.setOwner(original.getOwner());
        }
        if (item.getRequest() == null) {
            item.setRequest(original.getRequest());
        }
        return item;
    }

    public Item get(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет с таким id не существует"));
    }

    public ItemDtoWithBooking getWithBookings(Long id, Long userId) {
        var item = get(id);
        Booking current = null;
        Booking next = null;
        Booking last = null;
        if (item.getOwner().getId().equals(userId)) {
            current = itemRepository.findCurrentBookingByItem(item.getId());

            var bookings = itemRepository.findNextBookingByItem(item.getId());
            if (bookings.size() != 0) {
                next = bookings.get(0);
            }

            bookings = itemRepository.findLastBookingByItem(item.getId());
            if (bookings.size() != 0) {
                last = bookings.get(0);
            }
        }
        var dto = item.toDtoWithBookings(current, next, last);
        var comments = commentRepository.findAllByItem_Id(item.getId());
        var commentsDto = dto.getComments();
        for (Comment comment : comments) {
            commentsDto.add(comment.toDto());
        }

        dto.setComments(commentsDto);

        return dto;
    }

    public List<ItemDtoWithBooking> getAllWithBookings(Long userId) {
        userService.get(userId);
        var items = itemRepository.findAllByOwner_IdOrderById(userId);
        var result = new ArrayList<ItemDtoWithBooking>();
        for (Item item : items) {
            result.add(getWithBookings(item.getId(), item.getOwner().getId()));
        }
        return result;
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public void remove(Long id) {
        itemRepository.delete(get(id));
    }

    public Item create(ItemDto object, Long userId) {
        var item = object.toItem(null);
        validateItem(item);
        var user = userService.get(userId);
        item.setOwner(user);
        return itemRepository.save(item);
    }

    public Comment createComment(CommentInputDto object, Long itemId, Long userId) {
        if (object.getText().isEmpty()) {
            throw new ValidationException("Текст отзыва пустой");
        }

        var user = userService.get(userId);
        var item = get(itemId);

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

        if (oldestBooking.getEnd().isAfter(LocalDateTime.now())) {
            throw new ValidationException("У вас нет завершенных бронирований с этим предметом");
        }

        var comment = object.toComment(0L, user, item);
        return commentRepository.save(comment);
    }

    public Item patch(Item item, Long id, Long userId) {
        userService.get(userId);
        var original = get(id);

        if (!userId.equals(original.getOwner().getId())) {
            throw new NotFoundException("Пользователь не является владельцем");
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
}
