package com.example.restaurant.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderMealModel {
    private String name;
    private Integer quantity;
    private Double pricePerItem;
}
