package com.example.Backend.Repository;

import com.example.Backend.Entity.CartDetail;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    // ✅ 正確：從 CartDetail 走關聯到 Cart
    List<CartDetail> findByCart_UserIdAndCart_CartStatus(Long userId, byte cartStatus);

    // ✅ 檢查課程是否已在購物車中
    Optional<CartDetail> findByCart_CartIdAndCourse_CourseId(Long cartId, Long courseId);

    // ✅ JOIN FETCH 版本（解決 Lazy 問題）
    @Query("SELECT cd FROM CartDetail cd " +
            "JOIN FETCH cd.course c " +
            "JOIN FETCH cd.cart ca " +
            "WHERE ca.userId = :userId AND ca.cartStatus = :status")
    List<CartDetail> findActiveDetailsWithCourse(@Param("userId") Long userId,
            @Param("status") byte status);
}
