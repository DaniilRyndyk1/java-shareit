package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    public Booking create(BookingInputDto dto, Long userId) {
        var user = userService.get(userId);

        var itemId = dto.getItemId();
        var item = itemService.get(itemId);

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать вещь");
        }

        var booking = dto.toBooking(user, item);
        validateBooking(booking);
        booking.setStatus(BookingStatus.WAITING);
        return repository.save(booking);
    }

    public Booking approve(Long bookingId, Long userId, Boolean approved) {
        var booking = findById(bookingId);

        if (booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Пользователь является арендатором");
        } else if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Пользователь не является владельцем");
        } else if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Статус уже был изменен");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return repository.save(booking);
    }

    public Booking get(Long bookingId, Long userId) {
        var booking = findById(bookingId);

        userService.get(userId);
        var bookerId = booking.getBooker().getId();
        var ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new NotFoundException("Нет прав");
        }

        return booking;
    }

    public List<BookingDto> getBookingsByOwnerAndState(String state, Long userId) {
        userService.get(userId);
        var now = LocalDateTime.now();
        switch (state) {
            case "CURRENT":
                return repository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(userId, now, now);
            case "PAST":
                return repository.findAllByItem_Owner_IdAndEndLessThanEqualOrderByStartDesc(userId, now);
            case "FUTURE":
                return repository.findAllByItem_Owner_IdAndStartGreaterThanEqualOrderByStartDesc(userId, now);
            case "WAITING":
                return repository.findAllByItem_Owner_IdAndStatusIs(userId, BookingStatus.WAITING);
            case "REJECTED":
                return repository.findAllByItem_Owner_IdAndStatusIs(userId, BookingStatus.REJECTED);
            case "ALL":
                return repository.findAllByItem_Owner_IdOrderByEndDesc(userId);
            default:
                throw new UnsupportedStateException(state);
        }
    }

    public List<BookingDto> getBookingsByBookerAndState(String state, Long userId) {
        userService.get(userId);
        var now = LocalDateTime.now();
        switch (state) {
            case "CURRENT":
                return repository.findAllByBooker_IdAndStartBeforeAndEndAfter(userId, now, now);
            case "PAST":
                return repository.findAllByBooker_IdAndEndLessThanEqualOrderByStartDesc(userId, now);
            case "FUTURE":
                return repository.findAllByBooker_IdAndStartGreaterThanEqualOrderByStartDesc(userId, now);
            case "WAITING":
                return repository.findAllByBooker_IdAndStatusIs(userId, BookingStatus.WAITING);
            case "REJECTED":
                return repository.findAllByBooker_IdAndStatusIs(userId, BookingStatus.REJECTED);
            case "ALL":
                return repository.findAllByBooker_IdOrderByEndDesc(userId);
            default:
                throw new UnsupportedStateException(state);
        }
    }

    private void validateBooking(Booking booking) {
        if (booking.getStart() == null) {
            throw new ValidationException("Время начала не задано");
        } else if (booking.getEnd() == null) {
            throw new ValidationException("Время конца не задано");
        } else if (booking.getItem() == null) {
            throw new ValidationException("Предмет не задан");
        } else if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Предмет недоступен");
        } else if (booking.getBooker() == null) {
            throw new ValidationException("Пользователь не задан");
        } else if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Бронирование не может быть в прошлом");
        } else if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Конец бронирования не может быть раньше начала");
        } else if (booking.getEnd().isEqual(booking.getStart())) {
            throw new ValidationException("Конец бронирования не может быть равен началу");
        } else if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        }

        var start = booking.getStart();
        var end = booking.getEnd();
        var itemId = booking.getItem().getId();

        var sameByStart = repository.findFirstByItem_IdAndStartBeforeAndEndAfter(itemId, start, start);
        var sameByEnd = repository.findFirstByItem_IdAndStartBeforeAndEndAfter(itemId, end, end);
        if (sameByStart != null || sameByEnd != null) {
            throw new NotFoundException("Бронирование на это время уже есть");
        }
    }

    private Booking findById(Long bookingId) {
        return repository.findById(bookingId).
                orElseThrow(() -> new NotFoundException("Бронирование с таким id не существует"));
    }
}
