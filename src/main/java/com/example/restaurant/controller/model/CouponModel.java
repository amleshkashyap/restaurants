package com.example.restaurant.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class CouponModel {
    @NotNull
    private String couponCode;
    @NotNull
    private Date expirationDate;
    @NotNull
    private Double discountPercentage;
}
