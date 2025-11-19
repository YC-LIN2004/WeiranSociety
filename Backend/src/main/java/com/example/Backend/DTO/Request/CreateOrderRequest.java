package com.example.Backend.DTO.Request;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long userId;
    private List<Long> cartDetailIds;
    private BigDecimal finalAmount;
    private BigDecimal discountAmount;
    private String paymentMethod;
}
