package com.example.restaurant.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MealModel {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @Positive
    private Double price;
}
