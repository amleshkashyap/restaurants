package com.example.restaurant.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class OrderModel {
    @Positive
    private Double amount;
    @PositiveOrZero
    private Double tipAmount;
    private String couponId;
    @NotNull
    private ArrayList<OrderMealModel> orderMeals;
    @Positive
    private Long restaurantId;
    @NotNull
    private String status;
}
