package com.example.Backend.DTO.DTOS;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCouponDTO {
    private Integer id;
    private Long userId;
    private Integer couponId;
    private Integer orderId;
    private Boolean isUsed;
    private LocalDateTime useDate;
    private LocalDateTime createdAt;

    // 優惠券資訊
    private String code;
    private String couponName;
    private String description;
    private BigDecimal discountType;
    private BigDecimal discountValue;
    private BigDecimal minAmount;
    private String applicableScope;
    private LocalDateTime expiryDate;
    private String status;
}
