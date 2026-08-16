package com.example.restaurant.controller;

import com.example.restaurant.controller.dto.MealDTO;
import com.example.restaurant.controller.model.MealModel;
import com.example.restaurant.model.CurrentPrincipal;
import com.example.restaurant.model.Meal;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.repository.MealRepository;
import com.example.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/meals")
public class MealController {
    private final MealRepository mealRepository;
    private final RestaurantService restaurantService;

    public MealController(MealRepository mealRepository, RestaurantService restaurantService) {
        this.mealRepository = mealRepository;
        this.restaurantService = restaurantService;
    }

    @ModelAttribute("restaurant")
    public Restaurant resolveParent(@PathVariable Long restaurantId) {
        Restaurant restaurant = this.restaurantService.getRestaurantWithUser(restaurantId);
        if (restaurant == null) {
            throw new RuntimeException("Restaurant Not Found");
        }
        return restaurant;
    }

    @GetMapping
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<Iterable<MealDTO>> getMealsForRestaurant(
            @AuthenticationPrincipal CurrentPrincipal principal,
            @ModelAttribute("restaurant") Restaurant restaurant
    ) {
        Iterable<Meal> m = this.mealRepository.findMealsByRestaurantId(restaurant.getId());
        List<MealDTO> meals = new ArrayList<>();
        for (Meal meal: m) {
            meals.add(new MealDTO(meal));
        }
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<MealDTO> getMeal(
            @AuthenticationPrincipal CurrentPrincipal principal,
            @ModelAttribute("restaurant") Restaurant restaurant,
            @PathVariable Long restaurantId,
            @PathVariable Long id
    ) {
        return this.mealRepository.findMeal(restaurantId, id)
                .map(MealDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<MealDTO> createMeal(
            @AuthenticationPrincipal CurrentPrincipal principal,
            @ModelAttribute("restaurant") Restaurant restaurant,
            @Valid @RequestBody MealModel mealModel
    ) {
        Meal meal = new Meal(mealModel, restaurant);
        meal = this.mealRepository.save(meal);
        return ResponseEntity.ok(new MealDTO(meal));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<MealDTO> updateMeal(
            @AuthenticationPrincipal CurrentPrincipal principal,
            @ModelAttribute("restaurant") Restaurant restaurant,
            @PathVariable Long id,
            @Valid @RequestBody Meal meal
    ) {
        if (meal.getId() != null && !Objects.equals(meal.getId(), id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Meal storedMeal = this.mealRepository.findMeal(restaurant.getId(), id).orElse(null);
        if (storedMeal == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        meal.setRestaurant(restaurant);
        meal = this.mealRepository.save(meal);
        return ResponseEntity.ok(new MealDTO(meal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<HashMap<String, String>> deleteMeal(
            @AuthenticationPrincipal CurrentPrincipal principal,
            @ModelAttribute("restaurant") Restaurant restaurant,
            @PathVariable Long id
    ) {
        Meal storedMeal = this.mealRepository.findMeal(restaurant.getId(), id).orElse(null);
        HashMap<String, String> response = new HashMap<>();
        if (storedMeal != null) {
            this.mealRepository.delete(storedMeal);
            response.put("message", "Successfully deleted the meal.");
            return ResponseEntity.ok(response);
        }
        response.put("message", "Couldn't find the given meal");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
