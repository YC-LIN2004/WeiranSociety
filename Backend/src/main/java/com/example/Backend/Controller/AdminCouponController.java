package com.example.Backend.Controller;

import com.example.Backend.Entity.Coupon;
import com.example.Backend.Entity.CouponType;
import com.example.Backend.Service.CouponService;
import com.example.Backend.Service.CouponTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 後台優惠券管理 Controller
 * 處理管理員對優惠券和優惠券類型的 CRUD
 */
@CrossOrigin(origins = { "*" }, methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;
    private final CouponTypeService couponTypeService;

    // ==================== 優惠券類型管理 ====================

    /**
     * 查詢所有優惠券類型
     * GET /api/admin/coupons/types
     */
    @GetMapping("/types")
    public ResponseEntity<List<CouponType>> getAllCouponTypes() {
        return ResponseEntity.ok(couponTypeService.findAll());
    }

    /**
     * 根據ID查詢優惠券類型
     * GET /api/admin/coupons/types/{id}
     */
    @GetMapping("/types/{id}")
    public ResponseEntity<CouponType> getCouponTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(couponTypeService.findById(id));
    }

    /**
     * 創建優惠券類型
     * POST /api/admin/coupons/types
     */
    @PostMapping("/types")
    public ResponseEntity<CouponType> createCouponType(@Valid @RequestBody CouponType couponType) {
        CouponType created = couponTypeService.create(couponType);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * 更新優惠券類型
     * PATCH /api/admin/coupons/types/{id}
     */
    @PatchMapping("/types/{id}")
    public ResponseEntity<CouponType> updateCouponType(
            @PathVariable Integer id,
            @RequestBody CouponType couponType) {
        CouponType updated = couponTypeService.update(id, couponType);
        return ResponseEntity.ok(updated);
    }

    /**
     * 刪除優惠券類型
     * DELETE /api/admin/coupons/types/{id}
     */
    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteCouponType(@PathVariable Integer id) {
        couponTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 優惠券管理 ====================

    /**
     * 查詢所有優惠券
     * GET /api/admin/coupons
     */
    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.findAll());
    }

    /**
     * 根據ID查詢優惠券
     * GET /api/admin/coupons/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Integer id) {
        return ResponseEntity.ok(couponService.findById(id));
    }

    /**
     * 創建優惠券
     * POST /api/admin/coupons
     */
    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) {
        Coupon created = couponService.create(coupon);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * 更新優惠券
     * PATCH /api/admin/coupons/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(
            @PathVariable Integer id,
            @RequestBody Coupon coupon) {
        Coupon updated = couponService.update(id, coupon);
        return ResponseEntity.ok(updated);
    }

    /**
     * 刪除優惠券
     * DELETE /api/admin/coupons/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Integer id) {
        couponService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
