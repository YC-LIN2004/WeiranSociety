package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CouponID")
    private Integer id;

    @Column(name = "CouponTypeID", nullable = false)
    private Integer couponTypeId;

    @Column(name = "Code", length = 50, nullable = false, unique = true)
    private String code;

    // 總發行數量
    @Column(name = "Totalssued", nullable = false)
    private Integer totalIssued;

    // 已使用次數
    @Column(name = "UseCount", nullable = false)
    private Integer useCount = 0;

    // 券狀態：Active, Inactive, Expired
    @Column(name = "CouponStatus", length = 50, nullable = false)
    private String status;

    @Column(name = "ExpiryDate", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // 關聯到 CouponType
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CouponTypeID", insertable = false, updatable = false)
    private CouponType couponType;
}

