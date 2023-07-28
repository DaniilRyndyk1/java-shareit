package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = true)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = true)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    @Column(name = "review", nullable = true, length = 1024)
    private String review = "";
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public Booking() {

    }

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, String review, BookingStatus status) {
        this.setStart(start);
        this.setEnd(end);
        this.setItem(item);
        this.setBooker(booker);
        this.setReview(review);
        this.setStatus(status);
    }

    public BookingDto toDto() {
        var booking = new BookingDto(
                this.getStart(),
                this.getEnd(),
                this.getItem(),
                this.getBooker(),
                this.getReview(),
                this.getStatus()
        );
        booking.setId(getId());
        return booking;
    }
}
