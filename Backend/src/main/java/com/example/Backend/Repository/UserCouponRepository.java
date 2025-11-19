package com.example.Backend.Repository;

import com.example.Backend.Entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {
    
    // 查詢用戶的所有優惠券
    List<UserCoupon> findByUserId(Long userId);
    
    // 查詢用戶的可用優惠券（未使用且未過期）
    @Query("SELECT uc FROM UserCoupon uc " +
           "JOIN uc.coupon c " +
           "WHERE uc.userId = :userId " +
           "AND uc.isUsed = false " +
           "AND c.expiryDate > :now " +
           "AND c.status = 'Active'")
    List<UserCoupon> findAvailableByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // 查詢用戶已使用的優惠券
    List<UserCoupon> findByUserIdAndIsUsed(Long userId, Boolean isUsed);
    
    // 查詢用戶是否已領取特定優惠券
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Integer couponId);
    
    // 查詢用戶已過期的優惠券
    @Query("SELECT uc FROM UserCoupon uc " +
           "JOIN uc.coupon c " +
           "WHERE uc.userId = :userId " +
           "AND uc.isUsed = false " +
           "AND c.expiryDate <= :now")
    List<UserCoupon> findExpiredByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}

