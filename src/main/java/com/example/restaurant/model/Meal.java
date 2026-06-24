package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.restaurant.controller.model.MealModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
public class Meal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Double price;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Restaurant restaurant;

    public Meal(MealModel mealModel, Restaurant restaurant) {
        this.name = mealModel.getName();
        this.description = mealModel.getDescription();
        this.price = mealModel.getPrice();
        this.restaurant = restaurant;
    }
}
