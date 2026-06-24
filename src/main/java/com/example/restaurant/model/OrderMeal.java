package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.restaurant.controller.model.OrderMealModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "order_meals")
@Data
@NoArgsConstructor
public class OrderMeal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer quantity;

    @Column
    private Double pricePerItem;

    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Order order;

    public OrderMeal(OrderMealModel orderMealModel, Order order) {
        this.name = orderMealModel.getName();
        this.quantity = orderMealModel.getQuantity();
        this.pricePerItem = orderMealModel.getPricePerItem();
        this.order = order;
    }
}
