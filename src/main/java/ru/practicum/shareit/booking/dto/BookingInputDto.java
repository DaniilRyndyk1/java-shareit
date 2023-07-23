package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingInputDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

    public BookingInputDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        this.setStart(start);
        this.setEnd(end);
        this.setItemId(itemId);
    }

    public Booking toBooking(User user, Item item) {
        return new Booking(
                this.getStart(),
                this.getEnd(),
                item,
                user,
                null,
                null
        );
    }
}
