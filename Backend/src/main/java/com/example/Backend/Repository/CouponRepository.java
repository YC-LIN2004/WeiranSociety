package com.example.Backend.Repository;

import com.example.Backend.Entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    
    // 根據優惠券代碼查詢
    Optional<Coupon> findByCode(String code);
    
    // 查詢可用的優惠券（Active 且未過期）
    @Query("SELECT c FROM Coupon c WHERE c.status = 'Active' AND c.expiryDate > :now")
    List<Coupon> findAvailableCoupons(@Param("now") LocalDateTime now);
    
    // 查詢特定優惠券類型的所有券
    List<Coupon> findByCouponTypeId(Integer couponTypeId);
    
    // 查詢狀態為 Active 的優惠券
    List<Coupon> findByStatus(String status);
}

