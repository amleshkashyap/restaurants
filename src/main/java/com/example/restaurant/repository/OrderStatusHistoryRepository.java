package com.example.restaurant.repository;

import com.example.restaurant.model.OrderStatusHistory;
import org.springframework.data.repository.CrudRepository;

public interface OrderStatusHistoryRepository extends CrudRepository<OrderStatusHistory, Long> {
}
