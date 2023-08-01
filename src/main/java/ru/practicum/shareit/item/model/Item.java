package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "item")
@Data
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "item_request_id")
    private ItemRequest request;

    public Item() {

    }

    public ItemDto toDto() {
       return new ItemDto(id, name, description, available);
    }

    public ItemDtoWithBooking toDtoWithBookings(Booking current, Booking next, Booking last) {
        return new ItemDtoWithBooking(id, name, description, available, current, next, last, null);
    }
}
