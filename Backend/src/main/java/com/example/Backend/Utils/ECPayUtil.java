package com.example.Backend.Utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ECPayUtil {

    /**
     * 產生檢查碼 (CheckMacValue)
     */
    public String generateCheckMacValue(Map<String, String> params, String hashKey, String hashIv) {
        try {
            // 1. 參數排序（按照 key 的字母順序）
            String sortedParams = params.entrySet().stream()
                    .filter(e -> !"CheckMacValue".equals(e.getKey()))
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            // 2. 加上 HashKey 和 HashIV
            String rawString = "HashKey=" + hashKey + "&" + sortedParams + "&HashIV=" + hashIv;

            // 3. URL Encode（特殊處理）
            String encodedString = urlEncode(rawString);

            // 4. 轉小寫
            encodedString = encodedString.toLowerCase();

            // 5. SHA256 加密
            String checkMacValue = sha256(encodedString);

            // 6. 轉大寫
            return checkMacValue.toUpperCase();

        } catch (Exception e) {
            throw new RuntimeException("產生 CheckMacValue 失敗", e);
        }
    }

    /**
     * URL Encode（綠界特殊規則）
     */
    private String urlEncode(String value) {
        try {
            String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            // 綠界的特殊處理
            return encoded
                    .replace("%2d", "-")
                    .replace("%5f", "_")
                    .replace("%2e", ".")
                    .replace("%21", "!")
                    .replace("%2a", "*")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%20", "+");
        } catch (Exception e) {
            throw new RuntimeException("URL Encode 失敗", e);
        }
    }

    /**
     * SHA256 加密
     */
    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA256 加密失敗", e);
        }
    }

    /**
     * 產生商店訂單編號（唯一值）
     */
    public String generateMerchantTradeNo() {
        // 格式：日期時間 + 隨機數
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int) (Math.random() * 10000));
        return timestamp + random;
    }

    /**
     * 驗證回傳的 CheckMacValue
     */
    public boolean verifyCheckMacValue(Map<String, String> params, String hashKey, String hashIv) {
        String receivedCheckMacValue = params.get("CheckMacValue");
        if (receivedCheckMacValue == null) {
            return false;
        }

        String calculatedCheckMacValue = generateCheckMacValue(params, hashKey, hashIv);
        return receivedCheckMacValue.equalsIgnoreCase(calculatedCheckMacValue);
    }
}
