package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.restaurant.controller.model.RestaurantModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
public class Restaurant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String name;

    @Column
    private String description;

    @Column
    @NotNull
    private String address;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Coupon> coupons;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Meal> meals;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Restaurant(RestaurantModel restaurantModel, User user) {
        this.name = restaurantModel.getName();
        this.address = restaurantModel.getAddress();
        this.description = restaurantModel.getDescription();
        this.user = user;
        this.meals = new ArrayList<>();
        this.coupons = new ArrayList<>();
    }
}
