package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
public class BookingStateDto {
    private Long id;
    private Item item;
    private User booker;
    private String review;
    private BookingStatus status;

    public BookingStateDto(Item item, User booker, String review, BookingStatus status) {
        this.setItem(item);
        this.setBooker(booker);
        this.setReview(review);
        this.setStatus(status);
    }
}
