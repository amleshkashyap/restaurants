package com.example.restaurant.controller.dto;

import com.example.restaurant.model.OrderMeal;
import lombok.Data;

@Data
public class OrderMealDTO {
    private String name;
    private Integer quantity;
    private Double pricePerItem;

    public OrderMealDTO(OrderMeal orderMeal) {
        this.name = orderMeal.getName();
        this.quantity = orderMeal.getQuantity();
        this.pricePerItem = Math.round(orderMeal.getPricePerItem() * 100.0) / 100.0;
    }
}
