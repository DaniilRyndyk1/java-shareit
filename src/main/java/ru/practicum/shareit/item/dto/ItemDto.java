package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.base.Model;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@Data
public class ItemDto extends Model {
    private String description;
    private Boolean available;

    public ItemDto (String name, String description, Boolean available) {
        this.setName(name);
        this.setDescription(description);
        this.setAvailable(available);
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
