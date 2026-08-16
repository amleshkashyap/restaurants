package com.example.restaurant.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantModel {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String address;
}
