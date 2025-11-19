package com.example.Backend.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OrderDetail")
@Getter
@Setter
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderDetailID")
    private Long orderDetailId;

    // ✅ 一般系統內部訂單關聯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderID", nullable = true)
    @JsonIgnore
    private Order order;

    // ✅ 綠界支付訂單關聯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserOrderID", nullable = true)
    @JsonIgnore
    private UserOrder userOrder;

    // ✅ 課程關聯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseID", nullable = false)
    private Course course;

    // ✅ 數量
    @Column(nullable = false)
    private Integer quantity = 1;

    // ✅ 單價
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    // ✅ 建立時間
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ✅ 更新時間
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
