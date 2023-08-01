package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentInputDto {
    private String text;
    private Long itemId;

    public Comment toComment(Long id, User author, Item item) {
        return new Comment(id, text, item, author, LocalDateTime.now());
    }
}
