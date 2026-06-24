package com.example.restaurant.controller.dto;

import com.example.restaurant.model.Coupon;
import lombok.Data;

import java.sql.Date;

@Data
public class CouponDTO {
    private String couponCode;
    private Date expirationDate;
    private Double discountPercentage;

    public CouponDTO(Coupon coupon) {
        this.couponCode = coupon.getCouponCode();
        this.expirationDate = coupon.getExpirationDate();
        this.discountPercentage = coupon.getDiscountPercentage();
    }
}
