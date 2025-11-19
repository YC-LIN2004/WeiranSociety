package com.example.Backend.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "UserOrder")
@Data
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Long orderId;

    @Column(name = "UserID", nullable = false)
    private Long userId;

    @Column(name = "TotalAmount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "DiscountAmount")
    private BigDecimal discountAmount;

    @Column(name = "NetAmount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "OrderStatus", length = 20)
    private String orderStatus;

    @Column(name = "PaymentMethod", length = 50, nullable = false)
    private String paymentMethod;

    // === 綠界相關欄位 ===
    @Column(name = "MerchantTradeNo", length = 20)
    private String merchantTradeNo;

    @Column(name = "TradeNo", length = 20)
    private String tradeNo;

    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "PaymentType", length = 20)
    private String paymentType;

    @Column(name = "RtnCode")
    private Integer rtnCode;

    @Column(name = "RtnMsg", length = 200)
    private String rtnMsg;

    @Column(name = "TradeAmt")
    private BigDecimal tradeAmt;

    @Column(name = "CheckMacValue", columnDefinition = "TEXT")
    private String checkMacValue;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // ✅ 設定預設值（統一使用中文狀態）
        if (orderStatus == null) {
            orderStatus = "未付款";
        }

        // ✅ 重要：設定 discountAmount 預設值
        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}