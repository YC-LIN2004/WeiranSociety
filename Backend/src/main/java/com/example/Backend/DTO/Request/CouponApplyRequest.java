package com.example.Backend.DTO.Request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CouponApplyRequest {
    private Long userId;
    private List<Integer> userCouponIds; // 用戶選擇的優惠券ID列表
    private BigDecimal totalAmount; // 購物車總金額
}
