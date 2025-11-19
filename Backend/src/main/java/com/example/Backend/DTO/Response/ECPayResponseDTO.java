package com.example.Backend.DTO.Response;

import lombok.Data;
import java.util.Map;

@Data
public class ECPayResponseDTO {
    private String htmlForm; // 要提交給綠界的 HTML Form
    private String merchantTradeNo;
    private Long orderId;
    private Map<String, String> formData; // Form 資料
}