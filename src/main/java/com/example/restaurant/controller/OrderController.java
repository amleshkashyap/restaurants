package com.example.restaurant.controller;

import com.example.restaurant.controller.dto.OrderDTO;
import com.example.restaurant.controller.dto.OrderDetailsDTO;
import com.example.restaurant.controller.model.OrderModel;
import com.example.restaurant.filter.ResourceGuard;
import com.example.restaurant.model.*;
import com.example.restaurant.model.*;
import com.example.restaurant.repository.CouponRepository;
import com.example.restaurant.repository.RestaurantRepository;
import com.example.restaurant.repository.UserRepository;
import com.example.restaurant.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final CouponRepository couponRepository;
    private final RestaurantRepository restaurantRepository;
    private final ResourceGuard resourceGuard;
    private final UserRepository userRepository;

    public OrderController(
            OrderService orderService,
            CouponRepository couponRepository,
            RestaurantRepository restaurantRepository,
            ResourceGuard resourceGuard,
            UserRepository userRepository
    ) {
        this.orderService = orderService;
        this.couponRepository = couponRepository;
        this.restaurantRepository = restaurantRepository;
        this.resourceGuard = resourceGuard;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<OrderDTO>> getOrders(@AuthenticationPrincipal CurrentPrincipal principal, @RequestParam(required = false) Long restaurantId) {
        Iterable<Order> o;
        if (restaurantId != null) {
            o = this.orderService.getRestaurantOrders(restaurantId);
        } else if (principal.getRole().equals("ROLE_ADMIN")) {
            o = this.orderService.getAllOrders();
        } else {
            o = this.orderService.getUserOrders(this.userRepository.getReferenceById(principal.getId()));
        }
        List<OrderDTO> orders = new ArrayList<>();
        for (Order order: o) {
            orders.add(new OrderDTO(order));
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsDTO> getOrder(@AuthenticationPrincipal CurrentPrincipal principal, @PathVariable Long id) {
        Order order = this.orderService.getOrderWithAllUsers(id);
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!this.resourceGuard.checkOwnership(
                principal,
                order.getRestaurant().getUser().getEmail(),
                order.getUser().getEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(new OrderDetailsDTO(order));
    }

    @PostMapping
    public ResponseEntity<OrderDetailsDTO> createOrder(@AuthenticationPrincipal CurrentPrincipal principal, @RequestBody OrderModel orderModel) {
        Coupon coupon = this.couponRepository.findById(orderModel.getCouponId()).orElse(null);
        Restaurant restaurant = this.restaurantRepository.findById(orderModel.getRestaurantId()).orElse(null);
        if (restaurant == null || principal == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Order order = this.orderService.createOrder(
                orderModel,
                coupon,
                restaurant,
                this.userRepository.getReferenceById(principal.getId())
        );
        return ResponseEntity.ok(new OrderDetailsDTO(order));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderDTO> patchOrder(@AuthenticationPrincipal CurrentPrincipal principal, @PathVariable Long orderId, @RequestParam String status) {
        Order order = this.orderService.getOrderWithAllUsers(orderId);
        status = status.toUpperCase();
        OrderStatusValues curStatus = OrderStatusValues.valueOf(status);
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (curStatus.equals(OrderStatusValues.PLACED)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (!this.resourceGuard.checkOwnership(
                principal,
                order.getRestaurant().getUser().getEmail(),
                order.getUser().getEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (curStatus.equals(OrderStatusValues.RECEIVED)) {
            if (!this.resourceGuard.checkOwnership(principal, order.getUser().getEmail())) {
                // only the order creator and admin (not the restaurant owner) can mark the order as RECEIVED
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else if (curStatus.equals(OrderStatusValues.CANCELED)) {
            // do nothing, anyone can mark as CANCELED
        } else {
            if (!this.resourceGuard.checkOwnership(principal, order.getRestaurant().getUser().getEmail())) {
                // only the restaurant owner and admin (not the customer) can mark the order anything other than RECEIVED
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        OrderStatusValues lastStatus = order.getOrderStatusHistories().getLast().getStatus();
        if (!this.orderService.isValidTransition(lastStatus, curStatus)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new OrderDTO(this.orderService.updateOrderStatusHistory(order, status)));
    }
}
