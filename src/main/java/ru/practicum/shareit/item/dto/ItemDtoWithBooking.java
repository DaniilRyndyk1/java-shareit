package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDtoWithBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfoDto currentBooking;
    private BookingInfoDto nextBooking;
    private BookingInfoDto lastBooking;
    private List<CommentDto> comments = new ArrayList<>();

    public ItemDtoWithBooking(Long id, String name, String description, Boolean available, Booking current, Booking next, Booking last, List<CommentDto> comments) {
        this.setId(id);
        this.setName(name);
        this.setDescription(description);
        this.setAvailable(available);
        if (current != null) {
            this.setCurrentBooking(current.toDto().toInfo());
        }
        if (next != null) {
            this.setNextBooking(next.toDto().toInfo());
        }
        if (last != null) {
            this.setLastBooking(last.toDto().toInfo());
        }
        if (comments != null) {
            this.setComments(comments);
        }
    }
}
