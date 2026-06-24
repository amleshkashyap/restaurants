package com.example.restaurant.controller.dto;

import com.example.restaurant.model.Order;
import lombok.Data;

@Data
public class OrderDTO {
    private Long id;
    private Double amount;
    private RestaurantDTO restaurant;
    private String status;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.amount = Math.round(order.getAmount() * 100.0) / 100.0;
        this.restaurant = new RestaurantDTO(order.getRestaurant());
        this.status = order.getOrderStatusHistories().getLast().getStatus().name();
    }

    private Double getOriginalAmount(Double amount, Double discountPercentage) {
        return amount / (1.0 - discountPercentage);
    }
}
