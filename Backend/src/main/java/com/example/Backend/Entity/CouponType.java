package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CouponType")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CouponTypeID")
    private Integer id;

    @Column(name = "CouponName", length = 50, nullable = false)
    private String name;

    @Column(name = "CouponDescription", length = 200)
    private String description;

    // 折扣類型：1=固定金額, 2=百分比折扣
    @Column(name = "DiscountType", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountType;

    // 折扣值：若 type=1 則為金額(如200)，type=2 則為比例(如0.1代表9折)
    @Column(name = "DiscountValue", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountValue;

    // 最低消費金額門檻
    @Column(name = "MinAmount", precision = 10, scale = 2, nullable = false)
    private BigDecimal minAmount;

    // 適用範圍：ALL=全站, CATEGORY=特定分類, COURSE=特定課程
    @Column(name = "ApplicableScope", length = 50, nullable = false)
    private String applicableScope;

    @Column(name = "StartDate")
    private LocalDateTime startDate;

    @Column(name = "EndDate")
    private LocalDateTime endDate;
}

