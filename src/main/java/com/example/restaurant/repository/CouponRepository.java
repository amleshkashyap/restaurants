package com.example.restaurant.repository;

import com.example.restaurant.model.Coupon;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends CrudRepository<Coupon, String> {
    @Query(
            value = "SELECT * from coupons c where c.id = :id and c.restaurant_id = restaurantId",
            nativeQuery = true
    )
    public Optional<Coupon> findCoupon(@Param("restaurantId") Long restaurantId, @Param("id") String id);
    public Iterable<Coupon> findCouponsByRestaurantId(@Param("restaurantId") Long restaurantId);
}
