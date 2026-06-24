package com.example.restaurant.controller.dto;

import com.example.restaurant.model.Restaurant;
import lombok.Data;

@Data
public class RestaurantDTO {
    private Long id;
    private String name;
    private String description;
    private String address;

    public RestaurantDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.description = restaurant.getDescription();
    }
}
