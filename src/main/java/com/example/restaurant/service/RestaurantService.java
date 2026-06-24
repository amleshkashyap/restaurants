package com.example.restaurant.service;

import com.example.restaurant.controller.model.RestaurantModel;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final EntityManager entityManager;

    public RestaurantService(RestaurantRepository restaurantRepository, EntityManager entityManager) {
        this.restaurantRepository = restaurantRepository;
        this.entityManager = entityManager;
    }

    public Restaurant createRestaurant(RestaurantModel restaurantModel, User user) {
        Restaurant restaurant = new Restaurant(restaurantModel, user);
        restaurant = this.restaurantRepository.save(restaurant);
        restaurant.setUser(user);
        return restaurant;
    }

    public Restaurant getRestaurantWithDetails(Long id) {
        EntityGraph<Restaurant> graph = this.entityManager.createEntityGraph(Restaurant.class);
        graph.addAttributeNodes("coupons");
        graph.addAttributeNodes("meals");
        graph.addAttributeNodes("user");
        HashMap<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.loadgraph", graph);
        return this.entityManager.find(Restaurant.class, id, hints);
    }

    public Restaurant getRestaurantWithUser(Long id) {
        EntityGraph<Restaurant> graph = this.entityManager.createEntityGraph(Restaurant.class);
        graph.addAttributeNodes("user");
        HashMap<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.loadgraph", graph);
        return this.entityManager.find(Restaurant.class, id, hints);
    }
}
