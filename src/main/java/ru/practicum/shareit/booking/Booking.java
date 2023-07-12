package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.base.Model;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
public class Booking extends Model {
    private LocalDate start;
    private LocalDate end;
    private Item bookedItem;
    private User booker;
    private String review;
    private BookingStatus status;

    public Booking (LocalDate start, LocalDate end, Item bookedItem, User booker, String review, BookingStatus status) {
        this.setStart(start);
        this.setEnd(end);
        this.setBookedItem(bookedItem);
        this.setBooker(booker);
        this.setReview(review);
        this.setStatus(status);
    }
}
