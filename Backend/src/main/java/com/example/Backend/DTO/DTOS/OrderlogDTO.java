package com.example.Backend.DTO.DTOS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderlogDTO(
                Long orderId,
                Long userId,
                BigDecimal totalAmount,
                BigDecimal discountAmount,
                BigDecimal netAmount,
                String orderStatus,
                String paymentMethod,
                LocalDateTime createdAt,
                List<OrderItemDTO> items) {
}
