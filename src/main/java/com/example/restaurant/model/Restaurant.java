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

    public Restaurant (RestaurantBuilder restaurantBuilder) {
        this.id = restaurantBuilder.id;
        this.name = restaurantBuilder.name;
        this.description = restaurantBuilder.description;
        this.address = restaurantBuilder.address;
        this.coupons = restaurantBuilder.coupons;
        this.meals = restaurantBuilder.meals;
        this.user = restaurantBuilder.user;
    }

    public static class RestaurantBuilder {
        private final long id;
        private final String name;
        private final String description;
        private final String address;
        private final List<Coupon> coupons;
        private final List<Meal> meals;
        private final User user;

        public RestaurantBuilder(
                long id,
                String name,
                String description,
                String address,
                List<Coupon> coupons,
                List<Meal> meals,
                User user
        ) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.address = address;
            this.coupons = coupons;
            this.meals = meals;
            this.user = user;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }
}
