package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    public Item get(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет с таким id не существует"));
    }

    public ItemDtoWithBooking getWithBookings(Long id, Long userId) {
        var item = get(id);
        BookingInfoDto current = null;
        BookingInfoDto next = null;
        BookingInfoDto last = null;
        if (item.getOwner().getId().equals(userId)) {
            var currentData = bookingRepository.findCurrentByItem(item.getId());
            if (currentData != null) {
                current = bookingMapper.toInfo(currentData);
            }

            var bookings = bookingRepository.findNextBookingsByItem(item.getId());
            if (bookings.size() != 0) {
                next = bookingMapper.toInfo(bookings.get(0));
            }

            bookings = bookingRepository.findLastBookingsByItem(item.getId());
            if (bookings.size() != 0) {
                last = bookingMapper.toInfo(bookings.get(0));
            }
        }
        var dto = itemMapper.toDtoWithBookings(current, next, last, item);
        var comments = commentRepository.findAllByItem_Id(item.getId());
        var commentsDto = dto.getComments();
        for (Comment comment : comments) {
            commentsDto.add(comment.toDto());
        }

        dto.setComments(commentsDto);

        return dto;
    }

    public List<ItemDtoWithBooking> getAllWithBookings(Integer from, Integer size, Long userId) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("Переданы неверные параметры");
        }
        userService.get(userId);
        var items = itemRepository.findAllByOwner_IdOrderById(userId, PageRequest.of(from / size, size));
        var result = new ArrayList<ItemDtoWithBooking>();

        var comments = commentRepository.findByItemIn(items)
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        var currentBookings = bookingRepository.findCurrentBookingsByUser(userId)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        var lastBookings = bookingRepository.findLastBookingsByUser(userId)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        var nextBookings = bookingRepository.findNextBookingsByUser(userId)
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        for (Item item : items) {
            BookingInfoDto currentBooking = getBookingByItemFromMap(currentBookings, item);

            BookingInfoDto nextBooking = getBookingByItemFromMap(nextBookings, item);

            BookingInfoDto lastBooking = getBookingByItemFromMap(lastBookings, item);

            var itemDto = itemMapper.toDtoWithBookings(currentBooking, nextBooking, lastBooking, item);
            var itemComments = getCommentsFromMap(comments, item);
            itemDto.setComments(itemComments);

            result.add(itemDto);
        }

        return result;
    }

    public void remove(Long id) {
        itemRepository.delete(get(id));
    }

    public ItemDto create(ItemDto dto, Long userId) {
        var user = userService.get(userId);

        ItemRequest request = null;
        if (dto.getRequestId() != null) {
            request = itemRequestService.get(dto.getRequestId());
        }

        var item = itemMapper.toItem(user, dto, request);
        return itemMapper.toDto(itemRepository.save(item));
    }

    public CommentDto createComment(CommentInputDto dto, Long itemId, Long userId) {
        var user = userService.get(userId);
        var item = get(itemId);

        var bookings = bookingRepository.findAllByItemAndUser(itemId, userId);
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

        var comment = commentMapper.toComment(dto, -1L, item, user, now);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    public ItemDto patch(ItemDto dto, Long id, Long userId) {
        userService.get(userId);
        var item = get(id);

        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Пользователь не является владельцем");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return itemMapper.toDto(itemRepository.save(item));
    }

    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("Переданы неверные параметры");
        }
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        text = text.toLowerCase();
        return itemRepository.findByText(text, PageRequest.of(from / size, size)).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    private List<CommentDto> getCommentsFromMap(Map<Item, List<Comment>> comments, Item item) {
        return comments
                .entrySet()
                .stream()
                .filter(x -> Objects.equals(x.getKey().getId(), item.getId()))
                .findFirst()
                .map(x -> x
                        .getValue()
                        .stream()
                        .map(commentMapper::toDto)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    private BookingInfoDto getBookingByItemFromMap(Map<Item, List<Booking>> bookings, Item item) {
        var result = bookings
                .entrySet()
                .stream()
                .filter(x -> Objects.equals(x.getKey().getId(), item.getId()))
                .findFirst();

        return result
                .map(itemListEntry -> bookingMapper.toInfo(itemListEntry
                        .getValue()
                        .stream()
                        .findFirst()
                        .get()))
                .orElse(null);
    }
}
