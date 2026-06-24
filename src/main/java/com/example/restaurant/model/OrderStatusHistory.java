package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "order_status_histories")
@Data
@NoArgsConstructor
public class OrderStatusHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatusValues status;

    @Column
    private Date createdAt;

    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Order order;

    public OrderStatusHistory(String status, Order order) {
        this.createdAt = new Date(System.currentTimeMillis());
        this.status = OrderStatusValues.valueOf(status.toUpperCase());
        this.order = order;
    }
}
