package com.example.restaurant.service;

import com.example.restaurant.config.OrderStateMachineConfig;
import com.example.restaurant.controller.model.OrderMealModel;
import com.example.restaurant.controller.model.OrderModel;
import com.example.restaurant.model.*;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.repository.OrderStatusHistoryRepository;
import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Subgraph;
import jakarta.transaction.Transactional;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final EntityManager entityManager;
    private final StateMachineFactory<OrderStatusValues, OrderEvents> stateMachineFactory;

    public OrderService(
            OrderRepository orderRepository,
            OrderStatusHistoryRepository orderStatusHistoryRepository,
            EntityManager entityManager,
            StateMachineFactory<OrderStatusValues, OrderEvents> stateMachineFactory
    ) {
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.entityManager = entityManager;
        this.stateMachineFactory = stateMachineFactory;
    }

    @Transactional
    public Order createOrder(OrderModel orderModel, Coupon coupon, Restaurant restaurant, User user) {
        Order order = new Order(orderModel, user, coupon, restaurant);
        List<OrderMealModel> o = orderModel.getOrderMeals();
        List<OrderMeal> orderMeals = new ArrayList<>();
        for (OrderMealModel orderMealModel: o) {
            orderMeals.add(new OrderMeal(orderMealModel, order));
        }
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory(orderModel.getStatus(), order);
        order.setOrderMeals(orderMeals);
        order.setOrderStatusHistories(new ArrayList<>(List.of(orderStatusHistory)));
        order = this.orderRepository.save(order);
        return order;
    }

    public Order updateOrderStatusHistory(Order order, String status) {
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory(status, order);
        orderStatusHistory = this.orderStatusHistoryRepository.save(orderStatusHistory);
        List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();
        orderStatusHistories.add(orderStatusHistory);
        order.setOrderStatusHistories(orderStatusHistories);
        return order;
    }

    public Iterable<Order> getUserOrders(User user) {
        return this.orderRepository.findOrdersByUserId(user.getId());
    }

    public Iterable<Order> getRestaurantOrders(Long restaurantId) {
        return this.orderRepository.findOrdersByRestaurantId(restaurantId);
    }

    public Iterable<Order> getAllOrders() {
        return this.orderRepository.findAll();
    }

    public Order getOrder(Long id) {
        return this.orderRepository.findById(id).orElse(null);
    }

    public Order getOrderDetails(Long id) {
        EntityGraph<Order> graph = this.entityManager.createEntityGraph(Order.class);
        graph.addAttributeNodes("coupon");
        graph.addAttributeNodes("orderMeals");
        graph.addAttributeNodes("user");
        graph.addAttributeNodes("restaurant");
        graph.addAttributeNodes("orderStatusHistories");
        HashMap<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.loadgraph", graph);
        return this.entityManager.find(Order.class, id, hints);
    }

    public Order getOrderWithAllUsers(Long id) {
        EntityGraph<Order> graph = this.entityManager.createEntityGraph(Order.class);
        graph.addAttributeNodes("user");
        graph.addAttributeNodes("coupon");
        graph.addAttributeNodes("orderMeals");
        graph.addAttributeNodes("orderStatusHistories");
        Subgraph<Restaurant> restaurantSubgraph = graph.addSubgraph("restaurant");
        restaurantSubgraph.addAttributeNodes("user");
        HashMap<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.loadgraph", graph);
        return this.entityManager.find(Order.class, id, hints);
    }

    public boolean isValidTransition(OrderStatusValues lastStatus, OrderStatusValues curStatus) {
        Collection<Transition<OrderStatusValues, OrderEvents>> transitions = this.stateMachineFactory.getStateMachine().getTransitions();
        return transitions.stream().anyMatch(transition ->
                transition.getSource().getId().equals(lastStatus)
                && transition.getTarget().getId().equals(curStatus)
        );
    }

}
