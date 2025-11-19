package com.example.Backend.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        // ===== 基本查詢 =====

        /**
         * 根據用戶ID查詢所有訂單
         */
        List<Order> findByUserID(Long userID);

        /**
         * 根據訂單狀態查詢
         */
        List<Order> findByOrderStatus(String orderStatus);

        /**
         * 根據用戶ID和訂單狀態查詢
         */
        List<Order> findByUserIDAndOrderStatus(Long userID, String orderStatus);

        // ===== 排序查詢 =====

        /**
         * 根據用戶ID查詢，按創建時間降序排列
         */
        List<Order> findByUserIDOrderByCreatedAtDesc(Long userID);

        /**
         * 查詢最近的N筆訂單
         */
        List<Order> findTop10ByUserIDOrderByCreatedAtDesc(Long userID);

        // ===== 時間範圍查詢 =====

        /**
         * 查詢特定時間範圍內的訂單
         */
        List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        /**
         * 查詢用戶在特定時間範圍內的訂單
         */
        List<Order> findByUserIDAndCreatedAtBetween(
                        Long userID,
                        LocalDateTime startDate,
                        LocalDateTime endDate);

        // ===== 統計查詢 =====

        /**
         * 統計各狀態的訂單數量
         */
        long countByOrderStatus(String orderStatus);

        /**
         * 統計用戶的訂單數量
         */
        long countByUserID(Long userID);

        /**
         * 檢查用戶是否有未完成的訂單
         */
        boolean existsByUserIDAndOrderStatusNot(Long userID, String orderStatus);

        // ===== 自定義查詢 =====

        /**
         * 計算用戶總消費金額（僅計算已完成的訂單）
         */
        @Query("SELECT COALESCE(SUM(o.netAmount), 0) FROM Order o WHERE o.userID = :userID AND o.orderStatus = '完成'")
        BigDecimal calculateTotalSpentByUser(@Param("userID") Long userID);

        /**
         * 查詢每日訂單統計
         */
        @Query("SELECT DATE(o.createdAt) as orderDate, COUNT(o) as orderCount, SUM(o.netAmount) as totalAmount " +
                        "FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate " +
                        "GROUP BY DATE(o.createdAt)")
        List<Object[]> getDailyOrderStatistics(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // ===== 分頁查詢 =====

        /**
         * 分頁查詢用戶訂單
         */
        Page<Order> findByUserID(Long userID, Pageable pageable);

        /**
         * 分頁查詢特定狀態的訂單
         */
        Page<Order> findByOrderStatus(String orderStatus, Pageable pageable);
}