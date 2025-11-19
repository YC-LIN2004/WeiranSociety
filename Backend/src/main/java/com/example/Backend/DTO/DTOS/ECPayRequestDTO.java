package com.example.Backend.DTO.DTOS;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ECPayRequestDTO {
    private Long orderId;
    private String merchantTradeNo;
    private String tradeDesc;
    private String itemName;
    private Integer totalAmount;
    private String returnUrl;
    private String orderResultUrl;
    private String clientBackUrl;
}