package com.example.restaurant.controller;

import com.example.restaurant.controller.model.CouponModel;
import com.example.restaurant.filter.ResourceGuard;
import com.example.restaurant.model.Coupon;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.CouponRepository;
import com.example.restaurant.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.Arrays;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponRepository couponRepository;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private ResourceGuard resourceGuard;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test_GetCouponsForRestaurants() throws Exception {
        Coupon coupon1 = new Coupon.CouponBuilder("1", "Coupon 1", null, 10.0,null).build();
        Coupon coupon2 = new Coupon.CouponBuilder("2", "Coupon 2", null, 5.0, null).build();
        Restaurant r = mock(Restaurant.class);
        Mockito.when(resourceGuard.checkOwnership(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(r.getUser()).thenReturn(new User());
        Mockito.when(restaurantService.getRestaurantWithUser(1L)).thenReturn(r);
        Mockito.when(couponRepository.findCouponsByRestaurantId(Mockito.any())).thenReturn(Arrays.asList(coupon1, coupon2));

        String expectedJson = """
        [
          {
            "couponCode": "Coupon 1",
            "expirationDate": null,
            "discountPercentage": 10.0
          },
          {
            "couponCode": "Coupon 2",
            "expirationDate": null,
            "discountPercentage": 5.0
          }
        ]
        """;

        mockMvc.perform(get("/api/v1/restaurants/1/coupons"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    @Test
    void test_GetCoupon() throws Exception {
        Coupon coupon = new Coupon.CouponBuilder("1", "Coupon 1", null, 10.0,null).build();
        Restaurant r = mock(Restaurant.class);
        Mockito.when(resourceGuard.checkOwnership(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(r.getUser()).thenReturn(new User());
        Mockito.when(restaurantService.getRestaurantWithUser(1L)).thenReturn(r);
        Mockito.when(couponRepository.findCoupon(1L, "1")).thenReturn(java.util.Optional.of(coupon));

        String expectedJson = """
      {
        "couponCode": "Coupon 1",
        "expirationDate": null,
        "discountPercentage": 10.0
      }
      """;

        mockMvc.perform(get("/api/v1/restaurants/1/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    @Test
    void test_CreateCoupon() throws Exception {
        Date dt = new Date(System.currentTimeMillis());
        Coupon coupon = new Coupon.CouponBuilder("1", "Coupon 1", dt, 10.0,null).build();
        CouponModel couponModel = new CouponModel(
                "Coupon 1",
                dt,
                10.0
        );
        Restaurant r = mock(Restaurant.class);
        Mockito.when(resourceGuard.checkOwnership(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(r.getUser()).thenReturn(new User());
        Mockito.when(restaurantService.getRestaurantWithUser(1L)).thenReturn(r);
        Mockito.when(couponRepository.save(Mockito.any(Coupon.class))).thenReturn(coupon);

        String expectedJson = """
      {
        "couponCode": "Coupon 1",
        "expirationDate": %s,
        "discountPercentage": 10.0
      }
      """.formatted(dt);

        mockMvc.perform(post("/api/v1/restaurants/1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(couponModel)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    @Test
    void test_CreateCoupon_BadRequest() throws Exception {
        CouponModel couponModel = new CouponModel(
                "Coupon 1",
                null,
                10.0
        );
        Restaurant r = mock(Restaurant.class);
        Mockito.when(resourceGuard.checkOwnership(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(r.getUser()).thenReturn(new User());
        Mockito.when(restaurantService.getRestaurantWithUser(1L)).thenReturn(r);
        mockMvc.perform(post("/api/v1/restaurants/1/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(couponModel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_UpdateCoupon() throws Exception {

    }

    @Test
    void test_DeleteCoupon() throws Exception {

    }
}