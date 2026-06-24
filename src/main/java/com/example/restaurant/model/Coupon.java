package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.restaurant.controller.model.CouponModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
public class Coupon implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String couponCode;

    @Column
    private Date expirationDate;

    @Column
    private Double discountPercentage;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Restaurant restaurant;

    public Coupon(CouponModel couponModel, Restaurant restaurant) {
        this.couponCode = couponModel.getCouponCode();
        this.expirationDate = couponModel.getExpirationDate();
        this.discountPercentage = couponModel.getDiscountPercentage();
        this.restaurant = restaurant;
    }
}
