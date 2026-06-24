package com.example.restaurant.controller.dto;

import com.example.restaurant.model.Meal;
import lombok.Data;

@Data
public class MealDTO {
    private String name;
    private String description;
    private Double price;

    public MealDTO(Meal meal) {
        this.name = meal.getName();
        this.description = meal.getDescription();
        this.price = Math.round(meal.getPrice() * 100.0) / 100.0;
    }
}
