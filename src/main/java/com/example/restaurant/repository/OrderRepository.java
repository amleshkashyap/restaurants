package com.example.restaurant.repository;

import com.example.restaurant.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    public Iterable<Order> findOrdersByRestaurantId(Long restaurantId);
    public Iterable<Order> findOrdersByUserId(Long userId);
}
