package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private Item item;
    private User booker;
    private String review;
    private BookingStatus status;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingDto(LocalDateTime start, LocalDateTime end, Item item, User booker, String review, BookingStatus status) {
        this.setItem(item);
        this.setBooker(booker);
        this.setReview(review);
        this.setStatus(status);
        this.setStart(start);
        this.setEnd(end);
    }

    public BookingStateDto toStateDto() {
        var booking = new BookingStateDto(
                this.getItem(),
                this.getBooker(),
                this.getReview(),
                this.getStatus()
        );
        booking.setId(getId());
        return booking;
    }

    public BookingItemInfoDto toItemInfo() {
        return new BookingItemInfoDto(
                this.getId(),
                this.getBooker().getId()
        );
    }
}
