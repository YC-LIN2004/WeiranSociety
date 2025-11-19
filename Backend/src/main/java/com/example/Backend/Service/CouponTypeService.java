package com.example.Backend.Service;

import com.example.Backend.Entity.CouponType;
import com.example.Backend.Repository.CouponTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponTypeService {

    private final CouponTypeRepository couponTypeRepository;

    /**
     * 查詢所有優惠券類型
     */
    public List<CouponType> findAll() {
        return couponTypeRepository.findAll();
    }

    /**
     * 根據ID查詢優惠券類型
     */
    public CouponType findById(Integer id) {
        return couponTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到優惠券類型: " + id));
    }

    /**
     * 查詢有效期內的優惠券類型
     */
    public List<CouponType> findActiveCouponTypes() {
        LocalDateTime now = LocalDateTime.now();
        return couponTypeRepository.findByStartDateBeforeAndEndDateAfter(now, now);
    }

    /**
     * 創建優惠券類型
     */
    @Transactional
    public CouponType create(CouponType couponType) {
        // 檢查名稱是否重複
        if (couponTypeRepository.findByName(couponType.getName()) != null) {
            throw new IllegalArgumentException("優惠券類型名稱已存在: " + couponType.getName());
        }
        return couponTypeRepository.save(couponType);
    }

    /**
     * 更新優惠券類型
     */
    @Transactional
    public CouponType update(Integer id, CouponType request) {
        CouponType existing = findById(id);
        
        if (request.getName() != null) {
            CouponType duplicate = couponTypeRepository.findByName(request.getName());
            if (duplicate != null && !duplicate.getId().equals(id)) {
                throw new IllegalArgumentException("優惠券類型名稱已存在: " + request.getName());
            }
            existing.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getDiscountType() != null) {
            existing.setDiscountType(request.getDiscountType());
        }
        if (request.getDiscountValue() != null) {
            existing.setDiscountValue(request.getDiscountValue());
        }
        if (request.getMinAmount() != null) {
            existing.setMinAmount(request.getMinAmount());
        }
        if (request.getApplicableScope() != null) {
            existing.setApplicableScope(request.getApplicableScope());
        }
        if (request.getStartDate() != null) {
            existing.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            existing.setEndDate(request.getEndDate());
        }
        
        return couponTypeRepository.save(existing);
    }

    /**
     * 刪除優惠券類型
     */
    @Transactional
    public void delete(Integer id) {
        if (!couponTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("找不到優惠券類型: " + id);
        }
        couponTypeRepository.deleteById(id);
    }
}

