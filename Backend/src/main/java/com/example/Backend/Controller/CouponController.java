package com.example.Backend.Controller;

import com.example.Backend.DTO.DTOS.UserCouponDTO;
import com.example.Backend.DTO.Request.CouponApplyRequest;
import com.example.Backend.DTO.Response.CouponApplyResponse;
import com.example.Backend.Entity.Coupon;
import com.example.Backend.Entity.UserCoupon;
import com.example.Backend.Service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 前台優惠券 Controller
 * 處理用戶領取、查看、使用優惠券
 */
@CrossOrigin(origins = { "*" })
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * 前台：查詢可領取的優惠券列表
     * GET /api/coupons/available
     */
    @GetMapping("/available")
    public ResponseEntity<List<Coupon>> getAvailableCoupons() {
        List<Coupon> coupons = couponService.findAvailableCoupons();
        return ResponseEntity.ok(coupons);
    }

    /**
     * 前台：領取優惠券
     * POST /api/coupons/claim
     */
    @PostMapping("/claim")
    public ResponseEntity<?> claimCoupon(
            @RequestParam Long userId,
            @RequestParam Integer couponId) {
        try {
            UserCoupon userCoupon = couponService.claimCoupon(userId, couponId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "成功領取優惠券",
                    "data", userCoupon));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * 前台：查詢用戶的可使用優惠券
     * GET /api/coupons/my-coupons/available
     */
    @GetMapping("/my-coupons/available")
    public ResponseEntity<List<UserCouponDTO>> getMyAvailableCoupons(@RequestParam Long userId) {
        List<UserCouponDTO> coupons = couponService.findUserAvailableCoupons(userId);
        return ResponseEntity.ok(coupons);
    }

    /**
     * 前台：查詢用戶的已使用優惠券
     * GET /api/coupons/my-coupons/used
     */
    @GetMapping("/my-coupons/used")
    public ResponseEntity<List<UserCouponDTO>> getMyUsedCoupons(@RequestParam Long userId) {
        List<UserCouponDTO> coupons = couponService.findUserUsedCoupons(userId);
        return ResponseEntity.ok(coupons);
    }

    /**
     * 前台：查詢用戶的已過期優惠券
     * GET /api/coupons/my-coupons/expired
     */
    @GetMapping("/my-coupons/expired")
    public ResponseEntity<List<UserCouponDTO>> getMyExpiredCoupons(@RequestParam Long userId) {
        List<UserCouponDTO> coupons = couponService.findUserExpiredCoupons(userId);
        return ResponseEntity.ok(coupons);
    }

    /**
     * 前台：計算優惠券折扣（購物車使用）
     * POST /api/coupons/calculate
     */
    @PostMapping("/calculate")
    public ResponseEntity<CouponApplyResponse> calculateDiscount(
            @RequestBody CouponApplyRequest request) {
        CouponApplyResponse response = couponService.calculateDiscount(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 前台：使用優惠券（結帳時調用）
     * POST /api/coupons/use
     */
    @PostMapping("/use")
    public ResponseEntity<?> useCoupons(
            @RequestParam Long userId,
            @RequestParam List<Integer> userCouponIds,
            @RequestParam Integer orderId) {
        try {
            couponService.useCoupons(userId, userCouponIds, orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "優惠券使用成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }
}
