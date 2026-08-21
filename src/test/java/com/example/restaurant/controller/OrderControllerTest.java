package com.example.restaurant.controller;

import com.example.restaurant.controller.model.OrderMealModel;
import com.example.restaurant.controller.model.OrderModel;
import com.example.restaurant.filter.ResourceGuard;
import com.example.restaurant.model.*;
import com.example.restaurant.repository.CouponRepository;
import com.example.restaurant.repository.RestaurantRepository;
import com.example.restaurant.repository.UserRepository;
import com.example.restaurant.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
class OrderControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponRepository couponRepository;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ResourceGuard resourceGuard;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private StateMachineFactory<OrderStatusValues, OrderEvents> stateMachineFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private User admin;
    private User user;
    private User owner;
    private String jwtAdmin;
    private String jwtUser;
    private String jwtOwner;

    @BeforeEach
    void setUp() {
        admin = new User(1L, "admin", "admin", "admin@gmail.com", "admin", null, new Role("ROLE_ADMIN"));
        user = new User(2L, "user", "user", "user@gmail.com", "user", null, new Role("ROLE_USER"));
        owner = new User(3L, "owner", "owner", "owner@gmail.com", "owner", null, new Role("ROLE_OWNER"));
        Mockito.when(userRepository.findOneWithRolesByEmailIgnoreCase("admin@gmail.com")).thenReturn(java.util.Optional.of(admin));
        Mockito.when(userRepository.findOneWithRolesByEmailIgnoreCase("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        Mockito.when(userRepository.findOneWithRolesByEmailIgnoreCase("owner@gmail.com")).thenReturn(java.util.Optional.of(owner));
        jwtAdmin = buildJwt("admin@gmail.com", "ROLE_ADMIN");
        jwtUser = buildJwt("user@gmail.com", "ROLE_USER");
        jwtOwner = buildJwt("owner@gmail.com", "ROLE_OWNER");
    }

    @AfterEach
    void tearDown() {
    }

    private String buildJwt(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("5367566859703373367639792F423F452848284D6251655468576D5A71347437")), SignatureAlgorithm.HS256)
                .compact();
    }

    private Order setupAndGetOrder() {
        Restaurant r = new Restaurant();
        r.setUser(owner);
        Order o = new Order();
        o.setId(1L);
        o.setAmount(100.0);
        o.setTipAmount(10.0);
        o.setOrderMeals(new ArrayList<>());
        o.setRestaurant(r);
        o.setUser(user);
        OrderStatusHistory osh = new OrderStatusHistory();
        osh.setStatus(OrderStatusValues.PLACED);
        o.setOrderStatusHistories(List.of(osh));
        return o;
    }

    private OrderModel getOrderModel() {
        return new OrderModel(
                100.0,
                10.0,
                null,
                new ArrayList<OrderMealModel>(),
                1L,
                OrderStatusValues.PLACED.name()
        );
    }

    @Test
    void getOrders_Restaurant() throws Exception {
        Mockito.when(orderService.getRestaurantOrders(1L)).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/v1/orders?restaurantId=1")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void getOrders_Admin() throws Exception {
        Mockito.when(orderService.getAllOrders()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/v1/orders")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void getOrders_User() throws Exception {
        Order order = setupAndGetOrder();
        Mockito.when(orderService.getUserOrders(Mockito.any())).thenReturn(List.of(order));
        mockMvc.perform(get("/api/v1/orders")
                .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().isOk());
    }

    @Test
    void getOrder_NotFound() throws Exception {
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(null);
        mockMvc.perform(get("/api/v1/orders/1")
                .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrder_Unauthorized() throws Exception {
        Order order = setupAndGetOrder();
        User user2 = new User(4L, "user2", "user2", "user2@gmail.com", "user2", null, new Role("ROLE_USER"));
        order.setUser(user2);
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(order);
        mockMvc.perform(get("/api/v1/orders/1")
                .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getOrder_Success() throws Exception {
        Order order = setupAndGetOrder();
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(order);
        mockMvc.perform(get("/api/v1/orders/1")
                .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().isOk());
    }

    @Test
    void createOrder_NoRestaurant() throws Exception {
        OrderModel orderModel = getOrderModel();
        Mockito.when(couponRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(restaurantRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderModel))
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_Success() throws Exception {
        OrderModel orderModel = getOrderModel();
        Order order = setupAndGetOrder();
        Mockito.when(couponRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(restaurantRepository.findById(Mockito.any())).thenReturn(Optional.of(new Restaurant()));
        Mockito.when(orderService.createOrder(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(order);
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderModel))
                .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().isOk());
    }

    @Test
    void patchOrder_NotFound() throws Exception {
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(null);
        mockMvc.perform(patch("/api/v1/orders/1?status=CANCELED")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchOrder_StatusPlaced() throws Exception {
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(new Order());
        mockMvc.perform(patch("/api/v1/orders/1?status=PLACED")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchOrder_Unauthorized() throws Exception {
        Order order = setupAndGetOrder();
        User user2 = new User(4L, "user2", "user2", "user2@gmail.com", "user2", null, new Role("ROLE_USER"));
        order.setUser(user2);
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(order);
        mockMvc.perform(patch("/api/v1/orders/1?status=PROCESSING")
                .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchOrder_Unauthorized_Received() throws Exception {
        Order order = setupAndGetOrder();
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(order);
        mockMvc.perform(patch("/api/v1/orders/1?status=RECEIVED")
                        .header("Authorization", "Bearer " + jwtOwner))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchOrder_Unauthorized_NonReceived() throws Exception {
        Order order = setupAndGetOrder();
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(order);
        mockMvc.perform(patch("/api/v1/orders/1?status=PROCESSING")
                        .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchOrder_InvalidTransitions_Admin() throws Exception {
        Order order = setupAndGetOrder();
        Field field = OrderService.class.getDeclaredField("stateMachineFactory");
        field.setAccessible(true);
        field.set(orderService, stateMachineFactory);
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(order);
        Mockito.when(orderService.isValidTransition(Mockito.any(), Mockito.any())).thenCallRealMethod();
        mockMvc.perform(patch("/api/v1/orders/1?status=DELIVERED")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchOrder_ValidTransitions_Admin() throws Exception {
        Order order = setupAndGetOrder();
        Field field = OrderService.class.getDeclaredField("stateMachineFactory");
        field.setAccessible(true);
        field.set(orderService, stateMachineFactory);
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(order);
        Mockito.when(orderService.isValidTransition(Mockito.any(), Mockito.any())).thenCallRealMethod();
        Mockito.when(orderService.updateOrderStatusHistory(Mockito.any(), Mockito.any())).thenReturn(order);
        mockMvc.perform(patch("/api/v1/orders/1?status=PROCESSING")
                .header("Authorization", "Bearer " + jwtAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void patchOrder_Canceled_User() throws Exception {
        Order order = setupAndGetOrder();
        Field field = OrderService.class.getDeclaredField("stateMachineFactory");
        field.setAccessible(true);
        field.set(orderService, stateMachineFactory);
        Mockito.when(orderService.getOrderWithAllUsers(1L)).thenReturn(order);
        Mockito.when(orderService.isValidTransition(Mockito.any(), Mockito.any())).thenCallRealMethod();
        Mockito.when(orderService.updateOrderStatusHistory(Mockito.any(), Mockito.any())).thenReturn(order);
        mockMvc.perform(patch("/api/v1/orders/1?status=CANCELED")
                .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().isOk());
    }
}