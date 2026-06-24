package com.example.restaurant.controller.dto;

import com.example.restaurant.model.Coupon;
import com.example.restaurant.model.Meal;
import com.example.restaurant.model.Restaurant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RestaurantDetailsDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private List<CouponDTO> coupons;
    private List<MealDTO> meals;
    private UserDTO owner;

    public RestaurantDetailsDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.description = restaurant.getDescription();
        this.coupons = new ArrayList<>();
        for (Coupon coupon : restaurant.getCoupons()) {
            this.coupons.add(new CouponDTO(coupon));
        }
        this.meals = new ArrayList<>();
        for (Meal meal : restaurant.getMeals()) {
            this.meals.add(new MealDTO(meal));
        }
        this.owner = new UserDTO(restaurant.getUser());
    }
}
