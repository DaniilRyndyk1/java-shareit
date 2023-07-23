package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "item_request")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description",length = 512, nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
    @Column(name = "created", nullable = false)
    private LocalDate created;

    public ItemRequest() {

    }

    public ItemRequest(String description, User requestor, LocalDate created) {
        this.setDescription(description);
        this.setRequestor(requestor);
        this.setCreated(created);
    }
}
