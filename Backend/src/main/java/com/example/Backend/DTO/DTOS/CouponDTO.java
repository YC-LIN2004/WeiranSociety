package com.example.Backend.DTO.DTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponDTO {
    private Integer id;

    @NotNull(message = "優惠券類型ID不可為空")
    private Integer couponTypeId;

    @NotBlank(message = "優惠券代碼不可為空")
    private String code;

    @NotNull(message = "發行數量不可為空")
    @Positive(message = "發行數量必須為正數")
    private Integer totalIssued;

    private Integer useCount;

    @NotBlank(message = "狀態不可為空")
    private String status; // Active, Inactive, Expired

    @NotNull(message = "過期日期不可為空")
    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 關聯資料
    private CouponTypeDTO couponType;
}
