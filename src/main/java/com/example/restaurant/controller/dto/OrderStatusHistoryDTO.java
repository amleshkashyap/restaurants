package com.example.restaurant.controller.dto;

import com.example.restaurant.model.OrderStatusHistory;
import lombok.Data;

import java.sql.Date;

@Data
public class OrderStatusHistoryDTO {
    private Date createdAt;
    private String status;

    public OrderStatusHistoryDTO(OrderStatusHistory orderStatusHistory) {
        this.createdAt = orderStatusHistory.getCreatedAt();
        this.status = orderStatusHistory.getStatus().name();
    }
}
