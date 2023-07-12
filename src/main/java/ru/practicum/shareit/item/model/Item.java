package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.base.Model;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class Item extends Model {
    public String Description;
    public Boolean Available;
    public User Owner;
    public ItemRequest Request;

   public Item (String name, String description, Boolean available, User owner, ItemRequest request) {
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
}
