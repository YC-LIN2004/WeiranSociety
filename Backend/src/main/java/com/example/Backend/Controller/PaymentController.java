package com.example.Backend.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.example.Backend.DTO.Response.ECPayResponseDTO;
import com.example.Backend.Service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // ✅ 允許跨域
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 建立付款訂單
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> request) {
        try {
            log.info("📥 收到付款請求: {}", request);

            Object userIdObj = request.get("userId");
            Long userId = userIdObj != null ? Long.valueOf(userIdObj.toString()) : null;

            Object amountObj = request.get("amount");
            String itemName = String.valueOf(request.get("itemName"));

            java.math.BigDecimal amount;
            if (amountObj instanceof Integer) {
                amount = java.math.BigDecimal.valueOf((Integer) amountObj);
            } else if (amountObj instanceof Double) {
                amount = java.math.BigDecimal.valueOf((Double) amountObj);
            } else {
                amount = new java.math.BigDecimal(amountObj.toString());
            }

            ECPayResponseDTO response = paymentService.createOrderAndPayment(userId, amount, itemName);
            log.info("✅ 付款表單建立成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ 建立付款失敗", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "建立付款失敗",
                    "message", e.getMessage()));
        }
    }

    /**
     * ✅ 接收綠界付款結果通知（ReturnURL）
     * 綠界會用 POST 方式呼叫這個 API
     */
    @PostMapping(value = "/callback", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String handleCallback(@RequestParam Map<String, String> params) {
        log.info("📥 收到綠界付款回傳通知");
        log.info("  MerchantTradeNo: {}", params.get("MerchantTradeNo"));
        log.info("  RtnCode: {}", params.get("RtnCode"));
        log.info("  RtnMsg: {}", params.get("RtnMsg"));

        // 處理付款結果
        String result = paymentService.handlePaymentCallback(params);

        log.info("✅ 回傳給綠界: {}", result);
        return result;
    }

    /**
     * ✅ 付款完成後的返回頁面（OrderResultURL）
     * 綠界會導向這個頁面
     * 修改：測試環境下所有非明確失敗的狀態都視為成功
     */
    @PostMapping("/return")
    public RedirectView handleReturn(@RequestParam Map<String, String> params) {
        log.info("📥 用戶從綠界付款頁面返回");
        log.info("  MerchantTradeNo: {}", params.get("MerchantTradeNo"));
        log.info("  RtnCode: {}", params.get("RtnCode"));
        log.info("  RtnMsg: {}", params.get("RtnMsg"));

        // 取得付款狀態
        String rtnCodeStr = params.get("RtnCode");
        Integer rtnCode = Integer.parseInt(rtnCodeStr);

        // ✅ 修改邏輯：只有明確失敗才顯示 failed，其他都視為 success
        // RtnCode == 1: 正式環境成功
        // RtnCode == 800xxxx: 測試環境（Pending，視為成功）
        // RtnCode == 2, 3, 10100xxx: 明確失敗

        boolean isExplicitFailure = (rtnCode == 2 || rtnCode == 3 || (rtnCode >= 10100000 && rtnCode <= 10199999));
        String status = isExplicitFailure ? "failed" : "success";

        // 導向前端結果頁面
        String redirectUrl = "http://localhost:5173/payment/result?status=" + status
                + "&orderId=" + params.get("MerchantTradeNo")
                + "&rtnCode=" + rtnCode;

        log.info("🔄 導向前端: {} (RtnCode: {}, 判定為: {})", redirectUrl, rtnCode, status);

        return new RedirectView(redirectUrl);
    }

    /**
     * 查詢訂單狀態
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable Long orderId) {
        try {
            var order = paymentService.getOrderStatus(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "找不到訂單"));
        }
    }
}