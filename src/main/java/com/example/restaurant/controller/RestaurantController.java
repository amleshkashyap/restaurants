package com.example.restaurant.controller;

import com.example.restaurant.controller.dto.RestaurantDTO;
import com.example.restaurant.controller.dto.RestaurantDetailsDTO;
import com.example.restaurant.controller.model.RestaurantModel;
import com.example.restaurant.filter.ResourceGuard;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.RestaurantRepository;
import com.example.restaurant.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final RestaurantRepository restaurantRepository;
    private final ResourceGuard resourceGuard;

    public RestaurantController(RestaurantService restaurantService, RestaurantRepository restaurantRepository, ResourceGuard resourceGuard) {
        this.restaurantService = restaurantService;
        this.restaurantRepository = restaurantRepository;
        this.resourceGuard = resourceGuard;
    }

    @GetMapping
    public ResponseEntity<Iterable<RestaurantDTO>> getRestaurants() {
        Iterable<Restaurant> r = this.restaurantRepository.findAll();
        List<RestaurantDTO> restaurants = new ArrayList<>();
        for (Restaurant restaurant: r) {
            restaurants.add(new RestaurantDTO(restaurant));
        }
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDetailsDTO> getRestaurant(@PathVariable Long id) {
        return ResponseEntity.ok(new RestaurantDetailsDTO(this.restaurantService.getRestaurantWithDetails(id)));
    }

    @PostMapping
    public ResponseEntity<RestaurantDetailsDTO> createRestaurant(@ModelAttribute("currentUser") User user, @RequestBody RestaurantModel restaurantModel) {
        return ResponseEntity.ok(new RestaurantDetailsDTO(this.restaurantService.createRestaurant(restaurantModel, user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@ModelAttribute("currentUser") User user, @PathVariable Long id, @RequestBody Restaurant restaurant) {
        if (restaurant.getId() != null && !Objects.equals(restaurant.getId(), id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Restaurant storedRestaurant = this.restaurantService.getRestaurantWithUser(id);
        if (storedRestaurant == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!this.resourceGuard.checkOwnership(user, storedRestaurant.getUser().getEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        restaurant.setId(storedRestaurant.getId());
        restaurant = this.restaurantRepository.save(restaurant);
        return ResponseEntity.ok(new RestaurantDTO(restaurant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, String>> deleteRestaurant(@ModelAttribute("currentUser") User user, @PathVariable Long id) {
        Restaurant storedRestaurant = this.restaurantService.getRestaurantWithUser(id);
        HashMap<String, String> response = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        if (storedRestaurant == null) {
            status = HttpStatus.NOT_FOUND;
            response.put("message", "Couldn't Find The Given Restaurant.");
        } else if (!this.resourceGuard.checkOwnership(user, storedRestaurant.getUser().getEmail())) {
            status = HttpStatus.UNAUTHORIZED;
            response.put("message", "Unauthorized.");
        } else {
            this.restaurantRepository.delete(storedRestaurant);
            response.put("message", "Successfully Deleted The Restaurant.");
        }
        return new ResponseEntity<>(response, status);
    }
}
