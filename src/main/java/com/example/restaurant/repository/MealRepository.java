package com.example.restaurant.repository;

import com.example.restaurant.model.Meal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealRepository extends CrudRepository<Meal, Long> {
    @Query(
            value = "SELECT * from meals c where c.id = :id and c.restaurant_id = :restaurantId",
            nativeQuery = true
    )
    public Optional<Meal> findMeal(@Param("restaurantId") Long restaurantId, @Param("id") Long id);
    public Iterable<Meal> findMealsByRestaurantId(@Param("restaurantId") Long restaurantId);
}
