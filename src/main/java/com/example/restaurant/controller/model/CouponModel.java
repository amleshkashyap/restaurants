package com.example.restaurant.controller.model;

import lombok.Data;

import java.sql.Date;

@Data
public class CouponModel {
    private String couponCode;
    private Date expirationDate;
    private Double discountPercentage;
}
