package com.example.restaurant.controller;

import com.example.restaurant.controller.dto.CouponDTO;
import com.example.restaurant.controller.model.CouponModel;
import com.example.restaurant.model.Coupon;
import com.example.restaurant.model.CurrentPrincipal;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.CouponRepository;
import com.example.restaurant.service.RestaurantService;
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
@RequestMapping("/api/v1/restaurants/{restaurantId}/coupons")
public class CouponController {
    private final CouponRepository couponRepository;
    private final RestaurantService restaurantService;

    public CouponController(CouponRepository couponRepository, RestaurantService restaurantService) {
        this.couponRepository = couponRepository;
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
    public ResponseEntity<Iterable<CouponDTO>> getCouponsForRestaurant(@AuthenticationPrincipal CurrentPrincipal principal, @ModelAttribute("restaurant") Restaurant restaurant) {
        Iterable<Coupon> c = this.couponRepository.findCouponsByRestaurantId(restaurant.getId());
        List<CouponDTO> coupons = new ArrayList<>();
        for (Coupon coupon: c) {
            coupons.add(new CouponDTO(coupon));
        }
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<CouponDTO> getCoupon(@AuthenticationPrincipal CurrentPrincipal principal, @ModelAttribute("restaurant") Restaurant restaurant, @PathVariable Long restaurantId, @PathVariable String id) {
        return this.couponRepository.findCoupon(restaurantId, id)
                .map(CouponDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<CouponDTO> createCoupon(@AuthenticationPrincipal CurrentPrincipal principal, @ModelAttribute("restaurant") Restaurant restaurant, @RequestBody CouponModel couponModel) {
        Coupon coupon = new Coupon(couponModel, restaurant);
        coupon = this.couponRepository.save(coupon);
        return ResponseEntity.ok(new CouponDTO(coupon));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<CouponDTO> updateCoupon(@AuthenticationPrincipal CurrentPrincipal principal, @ModelAttribute("restaurant") Restaurant restaurant, @PathVariable Long restaurantId, @PathVariable String id, @RequestBody Coupon coupon) {
        if (coupon.getId() != null && !Objects.equals(coupon.getId(), id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Coupon storedCoupon = this.couponRepository.findCoupon(restaurantId, id).orElse(null);
        if (storedCoupon == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        coupon.setRestaurant(storedCoupon.getRestaurant());
        coupon = this.couponRepository.save(coupon);
        return ResponseEntity.ok(new CouponDTO(coupon));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@resourceGuard.checkOwnership(#principal, #restaurant.getUser().getEmail())")
    public ResponseEntity<HashMap<String, String>> deleteCoupon(@AuthenticationPrincipal CurrentPrincipal principal, @ModelAttribute("restaurant") Restaurant restaurant, @PathVariable String id) {
        Coupon storedCoupon = this.couponRepository.findCoupon(restaurant.getId(), id).orElse(null);
        HashMap<String, String> response = new HashMap<>();
        if (storedCoupon != null) {
            this.couponRepository.delete(storedCoupon);
            response.put("message", "Successfully deleted the coupon.");
            return ResponseEntity.ok(response);
        }
        response.put("message", "Couldn't find the given coupon");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
