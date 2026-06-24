package com.example.restaurant.controller.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderModel {
    private Double amount;
    private Double tipAmount;
    private String couponId;
    private ArrayList<OrderMealModel> orderMeals;
    private Long restaurantId;
    private String status;
}
