package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.restaurant.controller.model.CouponModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
    @NotNull
    private String couponCode;

    @Column
    @NotNull
    private Date expirationDate;

    @Column
    @NotNull
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

    public Coupon(CouponBuilder couponBuilder) {
        this.id = couponBuilder.id;
        this.couponCode = couponBuilder.couponCode;
        this.expirationDate = couponBuilder.expirationDate;
        this.discountPercentage = couponBuilder.discountPercentage;
        this.restaurant = couponBuilder.restaurant;
    }

    public static class CouponBuilder {
        private final String id;
        private final String couponCode;
        private final Date expirationDate;
        private final Double discountPercentage;
        private final Restaurant restaurant;

        public CouponBuilder(
                String id,
                String couponCode,
                Date expirationDate,
                Double discountPercentage,
                Restaurant restaurant
        ) {
            this.id = id;
            this.couponCode = couponCode;
            this.expirationDate = expirationDate;
            this.discountPercentage = discountPercentage;
            this.restaurant = restaurant;
        }

        public Coupon build() {
            return new Coupon(this);
        }
    }
}
