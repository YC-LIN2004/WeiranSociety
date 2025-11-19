package com.example.Backend.DTO.DTOS;

import java.math.BigDecimal;

public record OrderItemDTO(
                Long courseId,
                String title,
                BigDecimal unitPrice,
                Integer quantity,
                String coverUrl) {
}
