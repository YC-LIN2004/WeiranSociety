package com.example.Backend.DTO.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CouponApplyResponse {
    private BigDecimal originalAmount; // 原始金額
    private BigDecimal discountAmount; // 折扣金額
    private BigDecimal finalAmount; // 最終金額
    private List<String> appliedCoupons; // 已套用的優惠券代碼
    private String message; // 訊息（如：部分券不可用）

}
