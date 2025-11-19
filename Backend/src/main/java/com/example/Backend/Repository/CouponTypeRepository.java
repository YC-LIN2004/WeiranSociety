package com.example.Backend.Repository;

import com.example.Backend.Entity.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponTypeRepository extends JpaRepository<CouponType, Integer> {
    
    // 查詢有效期內的優惠券類型
    List<CouponType> findByStartDateBeforeAndEndDateAfter(LocalDateTime start, LocalDateTime end);
    
    // 根據名稱查詢
    CouponType findByName(String name);
}

