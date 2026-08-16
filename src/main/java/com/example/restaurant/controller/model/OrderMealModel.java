package com.example.restaurant.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderMealModel {
    @NotNull
    private String name;
    @Positive
    private Integer quantity;
    @Positive
    private Double pricePerItem;
}
