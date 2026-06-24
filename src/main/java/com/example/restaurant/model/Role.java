package com.example.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    private String name;

    public Role(String name) {
        this.name = name;
    }
}
