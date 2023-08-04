package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
@AllArgsConstructor
public class BookingMapper {
    public final ItemMapper itemMapper;
    public final UserMapper userMapper;

    public BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                itemMapper.toDto(booking.getItem()),
                userMapper.toDto(booking.getBooker()),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd());
    }

    public BookingInfoDto toInfo(Booking booking) {
        return new BookingInfoDto(
                booking.getId(),
                booking.getBooker().getId());
    }

    public Booking toBooking(User user, Item item, BookingInputDto booking) {
        return new Booking(
                -1L,
                booking.getStart(),
                booking.getEnd(),
                item,
                user,
                null);
    }
}
