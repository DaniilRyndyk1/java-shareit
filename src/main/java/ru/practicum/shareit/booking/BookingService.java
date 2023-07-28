package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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
        var startTimeStamp = booking.getStart().format(formatter);
        var endTimeStamp = booking.getEnd().format(formatter);
        var sameTimeBookingsByStart = repository.findAllByItemAndCrossDate(startTimeStamp, booking.getItem().getId());
        var sameTimeBookingsByEnd = repository.findAllByItemAndCrossDate(endTimeStamp, booking.getItem().getId());
        if (sameTimeBookingsByStart.size() > 0 || sameTimeBookingsByEnd.size() > 0) {
            throw new NotFoundException(0, "Бронирование на это время уже есть");
        }
    }

    public Booking create(BookingInputDto object, long userId) {
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        var item = itemService.get(object.getItemId());
        if (item == null) {
            throw new NotFoundException(userId, "Продукт с таким id не существует");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(userId, "Владелец не может забронировать вещь");
        }

        var booking = object.toBooking(user, item);
        validateBooking(booking);
        booking.setStatus(BookingStatus.WAITING);
        return repository.save(booking);
    }

    public Booking approve(long bookingId, Boolean approved, long userId) {
        var bookingOption = repository.findById(bookingId);
        if (bookingOption.isEmpty()) {
            throw new NotFoundException(userId, "Бронирование с таким id не существует");
        }
        var booking = bookingOption.get();
        if (booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException(userId, "Пользователь является арендатором");
        } else if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Пользователь не является владельцем");
        } else if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Статус уже был изменен");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return repository.save(booking);
    }

    public Booking get(long bookingId, long userId) {
        var bookingOption = repository.findById(bookingId);
        if (bookingOption.isEmpty()) {
            throw new NotFoundException(userId, "Бронирование с таким id не существует");
        }
        var booking = bookingOption.get();
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(userId, "Нет прав");
        }
        return booking;
    }

    public List<BookingDto> getItemsAll(String state, long userId) {
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        var bookings = repository.findAllByItem_Owner_IdOrderByEndDesc(userId);
        var now = LocalDateTime.now();
        switch (state) {
            case "CURRENT":
                return bookings.stream().filter(x -> x.getStart().isBefore(now) && x.getEnd().isAfter(now)).map(Booking::toDto).collect(Collectors.toList());
            case "PAST":
                return bookings.stream().filter(x -> now.isAfter(x.getEnd())).map(Booking::toDto).collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream().filter(x -> now.isBefore(x.getStart()) || now.isEqual(x.getStart())).map(Booking::toDto).collect(Collectors.toList());
            case "WAITING":
                return bookings.stream().filter(x -> x.getStatus().equals(BookingStatus.WAITING)).map(Booking::toDto).collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream().filter(x -> x.getStatus().equals(BookingStatus.REJECTED)).map(Booking::toDto).collect(Collectors.toList());
            case "ALL":
                return bookings.stream().map(Booking::toDto).collect(Collectors.toList());
            default:
                throw new UnsupportedStateException(state);
        }
    }

    public List<BookingDto> getAll(String state, long userId) {
        var user = userService.get(userId);
        if (user == null) {
            throw new NotFoundException(userId, "Пользователь с таким id не существует");
        }
        var bookings = repository.findAllByBooker_IdOrderByEndDesc(userId);
        var now = LocalDateTime.now();
        switch (state) {
            case "CURRENT":
                return bookings.stream().filter(x -> x.getStart().isBefore(now) && x.getEnd().isAfter(now)).map(Booking::toDto).collect(Collectors.toList());
            case "PAST":
                return bookings.stream().filter(x -> now.isAfter(x.getEnd())).map(Booking::toDto).collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream().filter(x -> now.isBefore(x.getEnd())).map(Booking::toDto).collect(Collectors.toList());
            case "WAITING":
                return bookings.stream().filter(x -> x.getStatus().equals(BookingStatus.WAITING)).map(Booking::toDto).collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream().filter(x -> x.getStatus().equals(BookingStatus.REJECTED)).map(Booking::toDto).collect(Collectors.toList());
            case "ALL":
                return bookings.stream().map(Booking::toDto).collect(Collectors.toList());
            default:
                throw new UnsupportedStateException(state);
        }
    }
}
