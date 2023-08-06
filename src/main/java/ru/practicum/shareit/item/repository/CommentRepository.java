package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem_Id(Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.item in :items ORDER BY c.created DESC")
    List<Comment> findByItemIn(List<Item> items);
}
