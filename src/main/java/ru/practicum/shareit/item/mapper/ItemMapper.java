package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

@Component
public class ItemMapper {
    public ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public ItemDtoWithBooking toDtoWithBookings(BookingInfoDto current, BookingInfoDto next, BookingInfoDto last, Item item) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                current,
                next,
                last,
                new ArrayList<>());
    }

    public Item toItem(User user, ItemDto item) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                user,
                null);
    }
}
