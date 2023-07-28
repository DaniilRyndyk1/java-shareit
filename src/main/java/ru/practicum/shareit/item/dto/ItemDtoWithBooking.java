package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDtoWithBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemInfoDto currentBooking;
    private BookingItemInfoDto nextBooking;
    private BookingItemInfoDto lastBooking;
    private List<CommentDto> comments = new ArrayList<>();

    public ItemDtoWithBooking(String name, String description, Boolean available, Booking current, Booking next, Booking last, List<CommentDto> comments) {
        this.setName(name);
        this.setDescription(description);
        this.setAvailable(available);
        if (current != null) {
            this.setCurrentBooking(current.toDto().toItemInfo());
        }
        if (next != null) {
            this.setNextBooking(next.toDto().toItemInfo());
        }
        if (last != null) {
            this.setLastBooking(last.toDto().toItemInfo());
        }
        if (comments != null) {
            this.setComments(comments);
        }
    }

    public Item toItem(User user) {
        Item item = new Item(
                this.getName(),
                this.getDescription(),
                this.getAvailable(),
                user,
                null
        );
        item.setId(getId());
        return item;
    }
}
