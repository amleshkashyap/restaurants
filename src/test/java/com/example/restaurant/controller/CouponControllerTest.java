package com.example.restaurant.controller;

import com.example.restaurant.filter.ResourceGuard;
import com.example.restaurant.model.Coupon;
import com.example.restaurant.model.Restaurant;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.CouponRepository;
import com.example.restaurant.service.RestaurantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
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
}