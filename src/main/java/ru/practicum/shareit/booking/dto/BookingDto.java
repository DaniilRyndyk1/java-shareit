package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Item item;
    private User booker;
    private BookingStatus status;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingInfoDto toInfo() {
        return new BookingInfoDto(id, booker.getId());
    }
}
