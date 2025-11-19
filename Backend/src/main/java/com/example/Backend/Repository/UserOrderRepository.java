package com.example.Backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.UserOrder;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

    /**
     * 根據商店訂單編號查詢
     */
    Optional<UserOrder> findByMerchantTradeNo(String merchantTradeNo);

    /**
     * 根據綠界交易編號查詢
     */
    Optional<UserOrder> findByTradeNo(String tradeNo);

    // ✅ 新增：支援前端訂單紀錄查詢

    /**
     * 根據用戶ID查詢所有訂單，按創建時間降序
     */
    List<UserOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根據用戶ID和訂單狀態查詢
     */
    List<UserOrder> findByUserIdAndOrderStatus(Long userId, String orderStatus);

    /**
     * 根據用戶ID查詢所有訂單
     */
    List<UserOrder> findByUserId(Long userId);

    /**
     * 統計各狀態的訂單數量
     */
    long countByOrderStatus(String orderStatus);

    /**
     * 統計用戶的訂單數量
     */
    long countByUserId(Long userId);
}