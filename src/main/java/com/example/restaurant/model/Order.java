package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.restaurant.controller.model.OrderModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order implements Serializable {
    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date date;

    @Column
    private Double amount;

    @Column
    private Double tipAmount;

    @JoinColumn(name = "coupon_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Coupon coupon;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "order")
    @JsonManagedReference
    private List<OrderMeal> orderMeals;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "order")
    @JsonManagedReference
    private List<OrderStatusHistory> orderStatusHistories;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Order(OrderModel orderModel, User user, Coupon coupon, Restaurant restaurant) {
        this.amount = orderModel.getAmount();
        this.tipAmount = orderModel.getTipAmount();
        this.coupon = coupon;
        this.date = new Date(System.currentTimeMillis());
        this.orderMeals = new ArrayList<>();
        this.orderStatusHistories = new ArrayList<>();
        this.restaurant = restaurant;
        this.user = user;
    }

    public void setAssociations(
            List<OrderMeal> orderMeals,
            Coupon coupon,
            User user,
            Restaurant restaurant,
            OrderStatusHistory orderStatusHistory
    ) {
        this.user = user;
        this.restaurant = restaurant;
        this.orderMeals = orderMeals;
        this.coupon = coupon;
        List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();
        orderStatusHistories.add(orderStatusHistory);
        this.orderStatusHistories = orderStatusHistories;
    }
}
