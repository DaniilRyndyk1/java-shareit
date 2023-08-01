package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingInputDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

    public Booking toBooking(User user, Item item) {
        return new Booking(-1L, start, end, item, user, null);
    }
}
