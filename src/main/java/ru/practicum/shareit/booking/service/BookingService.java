package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper mapper;

    public BookingDto create(BookingInputDto dto, Long userId) {
        var user = userService.get(userId);

        var itemId = dto.getItemId();
        var item = itemService.get(itemId);

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать вещь");
        }

        var booking = mapper.toBooking(user, item, dto);
        validateBooking(booking);
        booking.setStatus(BookingStatus.WAITING);
        return mapper.toDto(repository.save(booking));
    }

    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        var booking = findById(bookingId);

        if (booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("Пользователь является арендатором");
        } else if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Пользователь не является владельцем");
        } else if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Статус уже был изменен");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return mapper.toDto(repository.save(booking));
    }

    public BookingDto get(Long bookingId, Long userId) {
        var booking = findById(bookingId);

        userService.get(userId);
        var bookerId = booking.getBooker().getId();
        var ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new NotFoundException("Нет прав");
        }

        return mapper.toDto(booking);
    }

    public List<BookingDto> getBookingsByOwnerAndState(State state, Long userId, Integer from, Integer size) {
        userService.get(userId);
        var now = LocalDateTime.now();
        var pageRequest = PageRequest.of(from / size, size);
        Page<Booking> result;
        switch (state) {
            case CURRENT:
                result = repository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(userId, now, now, pageRequest);
                break;
            case PAST:
                result = repository.findAllByItem_Owner_IdAndEndLessThanEqualOrderByStartDesc(userId, now, pageRequest);
                break;
            case FUTURE:
                result = repository.findAllByItem_Owner_IdAndStartGreaterThanEqualOrderByStartDesc(userId, now, pageRequest);
                break;
            case WAITING:
                result = repository.findAllByItem_Owner_IdAndStatusIs(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                result = repository.findAllByItem_Owner_IdAndStatusIs(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                result = repository.findAllByItem_Owner_IdOrderByEndDesc(userId, pageRequest);
                break;
        }
        return result.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingsByBookerAndState(State state, Long userId, Integer from, Integer size) {
        userService.get(userId);
        var now = LocalDateTime.now();
        var pageRequest = PageRequest.of(from / size, size);
        Page<Booking> result;
        switch (state) {
            case CURRENT:
                result =  repository.findAllByBooker_IdAndStartBeforeAndEndAfter(userId, now, now, pageRequest);
                break;
            case PAST:
                result =  repository.findAllByBooker_IdAndEndLessThanEqualOrderByStartDesc(userId, now, pageRequest);
                break;
            case FUTURE:
                result =  repository.findAllByBooker_IdAndStartGreaterThanEqualOrderByStartDesc(userId, now, pageRequest);
                break;
            case WAITING:
                result =  repository.findAllByBooker_IdAndStatusIs(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                result =  repository.findAllByBooker_IdAndStatusIs(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                result =  repository.findAllByBooker_IdOrderByEndDesc(userId, pageRequest);
                break;
        }
        return result.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    private void validateBooking(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Предмет недоступен");
        } else if (booking.getBooker() == null) {
            throw new ValidationException("Пользователь не задан");
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
        return repository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с таким id не существует"));
    }
}
