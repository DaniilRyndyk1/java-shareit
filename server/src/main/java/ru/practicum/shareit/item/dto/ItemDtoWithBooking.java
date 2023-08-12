package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfoDto currentBooking;
    private BookingInfoDto nextBooking;
    private BookingInfoDto lastBooking;
    private List<CommentDto> comments;
}
