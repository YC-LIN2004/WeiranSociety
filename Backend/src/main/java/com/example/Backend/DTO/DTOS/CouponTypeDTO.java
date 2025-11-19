package com.example.Backend.DTO.DTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponTypeDTO {
    private Integer id;

    @NotBlank(message = "優惠券名稱不可為空")
    private String name;

    private String description;

    @NotNull(message = "折扣類型不可為空")
    private BigDecimal discountType; // 1=固定金額, 2=百分比

    @NotNull(message = "折扣值不可為空")
    @Positive(message = "折扣值必須為正數")
    private BigDecimal discountValue;

    @NotNull(message = "最低金額不可為空")
    private BigDecimal minAmount;

    @NotBlank(message = "適用範圍不可為空")
    private String applicableScope; // ALL, CATEGORY, COURSE

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
