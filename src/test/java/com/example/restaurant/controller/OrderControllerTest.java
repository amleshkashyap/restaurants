package com.example.restaurant.controller;

import com.example.restaurant.filter.ResourceGuard;
import com.example.restaurant.model.CurrentPrincipal;
import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.CouponRepository;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.repository.RestaurantRepository;
import com.example.restaurant.repository.UserRepository;
import com.example.restaurant.service.OrderService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockitoBean
    private ResourceGuard resourceGuard;

    @MockitoBean
    private UserRepository userRepository;

    private User user;
    private Role role;
    private String jwt;

    @BeforeEach
    void setUp() {
        user = new User(1L, "admin", "admin", "admin@gmail.com", "admin", null, null);
        role = new Role("ROLE_ADMIN");
        Mockito.when(userRepository.findOneWithRolesByEmailIgnoreCase("admin@gmail.com")).thenReturn(java.util.Optional.of(user));
        user.setRole(role);
        jwt = buildJwt("admin@gmail.com", "ROLE_ADMIN");
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

    @Test
    void getOrders_Restaurant() throws Exception {
        Mockito.when(orderService.getRestaurantOrders(1L)).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/v1/orders?restaurantId=1")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void getOrders_Admin() throws Exception {
        Mockito.when(orderService.getAllOrders()).thenReturn(new ArrayList<>());
        Mockito.when(userRepository.findOneWithRolesByEmailIgnoreCase("admin@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(get("/api/v1/orders")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void getOrders_User() throws Exception {
        jwt = buildJwt("user@gmail.com", "ROLE_USER");
        user.setEmail("user@gmail.com");
        role.setName("ROLE_USER");
        user.setRole(role);
        Mockito.when(orderService.getUserOrders(user)).thenReturn(new ArrayList<>());
        Mockito.when(userRepository.findOneWithRolesByEmailIgnoreCase("user@gmail.com")).thenReturn(java.util.Optional.of(user));
        mockMvc.perform(get("/api/v1/orders")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void getOrder() throws Exception {
    }

    @Test
    void createOrder() throws Exception {
    }

    @Test
    void patchOrder() throws Exception {
    }
}