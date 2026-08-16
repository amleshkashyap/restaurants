package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Account {
    @Id
    private long id;

    @JoinColumn(name = "user_id")
    @OneToOne
    private User user;

    @Column
    private String avatarUrl;

    @Column
    private Boolean sendEmailNotifications;
}
