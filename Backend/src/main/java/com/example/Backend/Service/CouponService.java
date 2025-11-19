package com.example.Backend.Service;

import com.example.Backend.DTO.DTOS.UserCouponDTO;
import com.example.Backend.DTO.Request.CouponApplyRequest;
import com.example.Backend.DTO.Response.CouponApplyResponse;
import com.example.Backend.Entity.Coupon;
import com.example.Backend.Entity.CouponType;
import com.example.Backend.Entity.UserCoupon;
import com.example.Backend.Repository.CouponRepository;
import com.example.Backend.Repository.CouponTypeRepository;
import com.example.Backend.Repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponTypeRepository couponTypeRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * 查詢所有優惠券
     */
    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    /**
     * 根據ID查詢優惠券
     */
    public Coupon findById(Integer id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到優惠券: " + id));
    }

    /**
     * 查詢可用的優惠券（前台顯示）
     */
    public List<Coupon> findAvailableCoupons() {
        return couponRepository.findAvailableCoupons(LocalDateTime.now());
    }

    /**
     * 創建優惠券
     */
    @Transactional
    public Coupon create(Coupon coupon) {
        // 檢查代碼是否重複
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new IllegalArgumentException("優惠券代碼已存在: " + coupon.getCode());
        }

        // 檢查優惠券類型是否存在
        if (!couponTypeRepository.existsById(coupon.getCouponTypeId())) {
            throw new IllegalArgumentException("優惠券類型不存在");
        }

        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());
        coupon.setUseCount(0);

        return couponRepository.save(coupon);
    }

    /**
     * 更新優惠券
     */
    @Transactional
    public Coupon update(Integer id, Coupon request) {
        Coupon existing = findById(id);

        if (request.getCode() != null && !request.getCode().equals(existing.getCode())) {
            if (couponRepository.findByCode(request.getCode()).isPresent()) {
                throw new IllegalArgumentException("優惠券代碼已存在: " + request.getCode());
            }
            existing.setCode(request.getCode());
        }

        if (request.getTotalIssued() != null) {
            existing.setTotalIssued(request.getTotalIssued());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getExpiryDate() != null) {
            existing.setExpiryDate(request.getExpiryDate());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return couponRepository.save(existing);
    }

    /**
     * 刪除優惠券
     */
    @Transactional
    public void delete(Integer id) {
        if (!couponRepository.existsById(id)) {
            throw new IllegalArgumentException("找不到優惠券: " + id);
        }
        couponRepository.deleteById(id);
    }

    /**
     * 用戶領取優惠券
     */
    @Transactional
    public UserCoupon claimCoupon(Long userId, Integer couponId) {
        // 檢查優惠券是否存在
        Coupon coupon = findById(couponId);

        // 檢查優惠券狀態
        if (!"Active".equals(coupon.getStatus())) {
            throw new IllegalArgumentException("此優惠券目前無法領取");
        }

        // 檢查是否已過期
        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("此優惠券已過期");
        }

        // 檢查是否已領取
        if (userCouponRepository.findByUserIdAndCouponId(userId, couponId).isPresent()) {
            throw new IllegalArgumentException("您已經領取過此優惠券");
        }

        // 檢查發行數量
        if (coupon.getUseCount() >= coupon.getTotalIssued()) {
            throw new IllegalArgumentException("此優惠券已被領取完畢");
        }

        // 創建用戶優惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setIsUsed(false);
        userCoupon.setCreatedAt(LocalDateTime.now());
        userCoupon.setUpdatedAt(LocalDateTime.now());

        UserCoupon saved = userCouponRepository.save(userCoupon);

        // 更新使用次數（這裡指領取次數）
        coupon.setUseCount(coupon.getUseCount() + 1);
        couponRepository.save(coupon);

        return saved;
    }

    /**
     * 查詢用戶的優惠券（可使用）
     */
    public List<UserCouponDTO> findUserAvailableCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findAvailableByUserId(userId, LocalDateTime.now());
        return convertToDTO(userCoupons);
    }

    /**
     * 查詢用戶的優惠券（已使用）
     */
    public List<UserCouponDTO> findUserUsedCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndIsUsed(userId, true);
        return convertToDTO(userCoupons);
    }

    /**
     * 查詢用戶的優惠券（已過期）
     */
    public List<UserCouponDTO> findUserExpiredCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findExpiredByUserId(userId, LocalDateTime.now());
        return convertToDTO(userCoupons);
    }

    /**
     * 計算優惠券折扣
     */
    public CouponApplyResponse calculateDiscount(CouponApplyRequest request) {
        CouponApplyResponse response = new CouponApplyResponse();
        response.setOriginalAmount(request.getTotalAmount());
        response.setAppliedCoupons(new ArrayList<>());

        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<String> messages = new ArrayList<>();

        // 取得用戶選擇的優惠券
        for (Integer userCouponId : request.getUserCouponIds()) {
            UserCoupon userCoupon = userCouponRepository.findById(userCouponId).orElse(null);

            if (userCoupon == null) {
                messages.add("優惠券不存在");
                continue;
            }

            // 檢查是否為該用戶的券
            if (!userCoupon.getUserId().equals(request.getUserId())) {
                messages.add("無效的優惠券");
                continue;
            }

            // 檢查是否已使用
            if (userCoupon.getIsUsed()) {
                messages.add("優惠券已使用: " + userCoupon.getCoupon().getCode());
                continue;
            }

            Coupon coupon = userCoupon.getCoupon();
            CouponType couponType = coupon.getCouponType();

            // 檢查是否過期
            if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
                messages.add("優惠券已過期: " + coupon.getCode());
                continue;
            }

            // 檢查最低消費金額
            if (request.getTotalAmount().compareTo(couponType.getMinAmount()) < 0) {
                messages.add("未達最低消費金額: " + coupon.getCode());
                continue;
            }

            // 計算折扣
            BigDecimal discount = BigDecimal.ZERO;
            if (couponType.getDiscountType().compareTo(BigDecimal.ONE) == 0) {
                // 固定金額折扣
                discount = couponType.getDiscountValue();
            } else if (couponType.getDiscountType().compareTo(new BigDecimal("2")) == 0) {
                // 百分比折扣
                discount = request.getTotalAmount()
                        .multiply(couponType.getDiscountValue())
                        .setScale(0, RoundingMode.HALF_UP);
            }

            totalDiscount = totalDiscount.add(discount);
            response.getAppliedCoupons().add(coupon.getCode());
        }

        response.setDiscountAmount(totalDiscount);
        response.setFinalAmount(request.getTotalAmount().subtract(totalDiscount).max(BigDecimal.ZERO));

        if (!messages.isEmpty()) {
            response.setMessage(String.join("; ", messages));
        }

        return response;
    }

    /**
     * 使用優惠券（結帳時調用）
     */
    @Transactional
    public void useCoupons(Long userId, List<Integer> userCouponIds, Integer orderId) {
        for (Integer userCouponId : userCouponIds) {
            UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                    .orElseThrow(() -> new IllegalArgumentException("找不到優惠券"));

            if (!userCoupon.getUserId().equals(userId)) {
                throw new IllegalArgumentException("無效的優惠券");
            }

            if (userCoupon.getIsUsed()) {
                throw new IllegalArgumentException("優惠券已使用");
            }

            userCoupon.setIsUsed(true);
            userCoupon.setUseDate(LocalDateTime.now());
            userCoupon.setOrderId(orderId);
            userCoupon.setUpdatedAt(LocalDateTime.now());

            userCouponRepository.save(userCoupon);
        }
    }

    /**
     * 將 UserCoupon 轉換為 DTO
     */
    private List<UserCouponDTO> convertToDTO(List<UserCoupon> userCoupons) {
        return userCoupons.stream().map(uc -> {
            UserCouponDTO dto = new UserCouponDTO();
            dto.setId(uc.getId());
            dto.setUserId(uc.getUserId());
            dto.setCouponId(uc.getCouponId());
            dto.setOrderId(uc.getOrderId());
            dto.setIsUsed(uc.getIsUsed());
            dto.setUseDate(uc.getUseDate());
            dto.setCreatedAt(uc.getCreatedAt());

            if (uc.getCoupon() != null) {
                Coupon coupon = uc.getCoupon();
                dto.setCode(coupon.getCode());
                dto.setExpiryDate(coupon.getExpiryDate());
                dto.setStatus(coupon.getStatus());

                if (coupon.getCouponType() != null) {
                    CouponType type = coupon.getCouponType();
                    dto.setCouponName(type.getName());
                    dto.setDescription(type.getDescription());
                    dto.setDiscountType(type.getDiscountType());
                    dto.setDiscountValue(type.getDiscountValue());
                    dto.setMinAmount(type.getMinAmount());
                    dto.setApplicableScope(type.getApplicableScope());
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }
}
