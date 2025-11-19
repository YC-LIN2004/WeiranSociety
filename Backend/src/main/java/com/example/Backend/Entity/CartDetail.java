package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "CartDetail", schema = "dbo", uniqueConstraints = @UniqueConstraint(columnNames = { "CartID",
        "CourseID" }))
@Getter
@Setter
public class CartDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CartDetailID")
    private Long cartDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CartID", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Cart cart;

    // 合併時，請重新啟用 Course 關聯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseID", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Course course;

    @Column(name = "Price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price = BigDecimal.ZERO;// 金額必須有值

    @Column(name = "AddedAt")
    private LocalDateTime addedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }
}