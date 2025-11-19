package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserCoupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserCouponID")
    private Integer id;

    @Column(name = "UserID", nullable = false)
    private Long userId;

    @Column(name = "CouponID", nullable = false)
    private Integer couponId;

    @Column(name = "OrderID")
    private Integer orderId;

    // 是否已使用
    @Column(name = "IsUsed", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "UseDate")
    private LocalDateTime useDate;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // 關聯到 Coupon
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CouponID", insertable = false, updatable = false)
    private Coupon coupon;

    // 關聯到 Users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", insertable = false, updatable = false)
    private Users user;
}

