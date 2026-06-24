package com.example.restaurant.controller.dto;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderMeal;
import com.example.restaurant.model.OrderStatusHistory;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDetailsDTO {
    private Long id;
    private Double amount;
    private Double tipAmount;
    private CouponDTO coupon;
    private Double discountAmount;
    private List<OrderMealDTO> orderMeals;
    private List<OrderStatusHistoryDTO> orderStatusHistories;
    private RestaurantDTO restaurant;
    private UserDTO user;
    private String status;

    public OrderDetailsDTO(Order order) {
        this.id = order.getId();
        this.amount = Math.round(order.getAmount() * 100.0) / 100.0;
        this.restaurant = new RestaurantDTO(order.getRestaurant());
        this.status = order.getOrderStatusHistories().getLast().getStatus().name();
        this.tipAmount = Math.round(order.getTipAmount() * 100.0) / 100.0;
        if (order.getCoupon() != null) {
            this.coupon = new CouponDTO(order.getCoupon());
            this.discountAmount = Math.round((this.getOriginalAmount(this.amount, this.coupon.getDiscountPercentage()) - this.amount) * 100.0) / 100.0;
        }
        this.orderMeals = new ArrayList<>();
        for (OrderMeal orderMeal : order.getOrderMeals()) {
            this.orderMeals.add(new OrderMealDTO(orderMeal));
        }
        this.user = new UserDTO(order.getUser());
        this.orderStatusHistories = new ArrayList<>();
        for (OrderStatusHistory orderStatusHistory: order.getOrderStatusHistories()) {
            this.orderStatusHistories.add(new OrderStatusHistoryDTO(orderStatusHistory));
        }
    }

    private Double getOriginalAmount(Double amount, Double discountPercentage) {
        return amount / (1.0 - discountPercentage);
    }
}
