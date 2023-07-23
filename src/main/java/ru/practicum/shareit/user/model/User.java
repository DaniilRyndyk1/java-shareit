package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "email", length = 512, nullable = false, unique = true)
    private String email;

    public User() {

    }

    public User(String name, String email) {
        this.setName(name);
        this.setEmail(email);
    }
}
