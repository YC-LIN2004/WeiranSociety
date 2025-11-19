package com.example.Backend.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.Backend.DTO.DTOS.ECPayRequestDTO;
import com.example.Backend.DTO.Response.ECPayResponseDTO;
import com.example.Backend.Utils.ECPayUtil;
import com.example.Backend.config.ECPayConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ECPayService {

    private final ECPayConfig ecPayConfig;
    private final ECPayUtil ecPayUtil;

    /**
     * 產生綠界付款表單
     */
    public ECPayResponseDTO createPaymentForm(ECPayRequestDTO request) {
        try {
            // 準備參數
            Map<String, String> params = new LinkedHashMap<>();

            // 必要參數
            params.put("MerchantID", ecPayConfig.getMerchantId());
            params.put("MerchantTradeNo", request.getMerchantTradeNo());
            params.put("MerchantTradeDate", getCurrentTimestamp());
            params.put("PaymentType", "aio");
            params.put("TotalAmount", String.valueOf(request.getTotalAmount()));
            params.put("TradeDesc", request.getTradeDesc() != null ? request.getTradeDesc() : "線上課程");
            params.put("ItemName", request.getItemName() != null ? request.getItemName() : "課程");
            params.put("ReturnURL", request.getReturnUrl());
            params.put("OrderResultURL", request.getOrderResultUrl());
            params.put("ClientBackURL", request.getClientBackUrl());
            params.put("ChoosePayment", "ALL"); // 可選：Credit, ATM, CVS, BARCODE, ALL
            params.put("EncryptType", "1"); // 使用 SHA256

            // 信用卡相關參數（可選）
            params.put("NeedExtraPaidInfo", "N");
            params.put("IgnorePayment", "GooglePay#ApplePay"); // 排除某些支付方式

            // ✅ 記錄參數以便除錯
            log.info("📝 綠界付款參數:");
            params.forEach((key, value) -> {
                if (!"CheckMacValue".equals(key)) {
                    log.info("  {} = {}", key, value);
                }
            });

            // 產生檢查碼
            String checkMacValue = ecPayUtil.generateCheckMacValue(
                    params,
                    ecPayConfig.getHashKey(),
                    ecPayConfig.getHashIv());
            params.put("CheckMacValue", checkMacValue);

            log.info("✅ CheckMacValue: {}", checkMacValue);

            // 產生 HTML Form
            String htmlForm = generateHtmlForm(params);

            // 建立回應
            ECPayResponseDTO response = new ECPayResponseDTO();
            response.setHtmlForm(htmlForm);
            response.setMerchantTradeNo(request.getMerchantTradeNo());
            response.setOrderId(request.getOrderId());
            response.setFormData(params);

            log.info("✅ 綠界付款表單建立成功 - 訂單編號: {}", request.getMerchantTradeNo());

            return response;

        } catch (Exception e) {
            log.error("❌ 建立綠界付款表單失敗", e);
            throw new RuntimeException("建立付款表單失敗: " + e.getMessage());
        }
    }

    /**
     * 驗證綠界回傳資料
     */
    public boolean verifyCallback(Map<String, String> params) {
        try {
            boolean isValid = ecPayUtil.verifyCheckMacValue(
                    params,
                    ecPayConfig.getHashKey(),
                    ecPayConfig.getHashIv());

            if (isValid) {
                log.info("✅ 綠界回傳資料驗證成功 - 訂單編號: {}",
                        params.get("MerchantTradeNo"));
            } else {
                log.warn("⚠️ 綠界回傳資料驗證失敗 - 訂單編號: {}",
                        params.get("MerchantTradeNo"));
            }

            return isValid;

        } catch (Exception e) {
            log.error("❌ 驗證綠界回傳資料時發生錯誤", e);
            return false;
        }
    }

    /**
     * 產生 HTML Form
     * ✅ 修正版本：加入 null 檢查
     */
    private String generateHtmlForm(Map<String, String> params) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("<meta charset=\"UTF-8\">\n");
            html.append("<title>正在前往付款頁面...</title>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("<form id=\"ecpayForm\" method=\"post\" action=\"")
                    .append(ecPayConfig.getApiUrl())
                    .append("\">\n");

            // ✅ 加入所有參數（加上 null 檢查）
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // ✅ NULL 檢查
                if (value == null) {
                    log.warn("⚠️ 參數 [{}] 的值為 null，使用空字串", key);
                    value = "";
                }

                // ✅ HTML 特殊字符轉義
                value = escapeHtml(value);

                html.append("<input type=\"hidden\" name=\"")
                        .append(key)
                        .append("\" value=\"")
                        .append(value)
                        .append("\">\n");
            }

            html.append("</form>\n");
            html.append("<script>\n");
            html.append("document.getElementById('ecpayForm').submit();\n");
            html.append("</script>\n");
            html.append("</body>\n");
            html.append("</html>");

            return html.toString();

        } catch (Exception e) {
            log.error("❌ 產生 HTML 表單時發生錯誤", e);
            throw new RuntimeException("產生 HTML 表單失敗: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ HTML 特殊字符轉義
     */
    private String escapeHtml(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * 取得當前時間戳記（綠界格式）
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date());
    }
}