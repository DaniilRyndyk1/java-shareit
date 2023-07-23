package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private String review;
    private BookingStatus status;

    public BookingDto(LocalDateTime start, LocalDateTime end, Item item, User booker, String review, BookingStatus status) {
        this.setStart(start);
        this.setEnd(end);
        this.setItem(item);
        this.setBooker(booker);
        this.setReview(review);
        this.setStatus(status);
    }

    public Booking toBooking(Item item, User booker) {
        Booking booking = new Booking(
                this.getStart(),
                this.getEnd(),
                item,
                booker,
                this.getReview(),
                this.getStatus()
        );
        booking.setId(getId());
        return booking;
    }
}
