package com.example.restaurant.repository;

import com.example.restaurant.model.OrderMeal;
import org.springframework.data.repository.CrudRepository;

public interface OrderMealRepository extends CrudRepository<OrderMeal, Long> {
}
