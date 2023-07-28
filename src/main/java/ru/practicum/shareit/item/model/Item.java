package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "item")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "item_request_id")
    private ItemRequest request;

    public Item() {

    }

   public Item(String name, String description, Boolean available, User owner, ItemRequest request) {
       this.setName(name);
       this.setDescription(description);
       this.setAvailable(available);
       this.setOwner(owner);
       this.setRequest(request);
   }

   public ItemDto toDto() {
       var item =  new ItemDto(
               this.getName(),
               this.getDescription(),
               this.getAvailable()
       );
       item.setId(getId());
       return item;
   }

    public ItemDtoWithBooking toDtoWithBookings(Booking current, Booking next, Booking last) {
        var item =  new ItemDtoWithBooking(
                this.getName(),
                this.getDescription(),
                this.getAvailable(),
                current,
                next,
                last,
                null
        );
        item.setId(getId());
        return item;
    }
}
