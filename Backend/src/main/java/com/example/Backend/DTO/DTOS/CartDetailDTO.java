package com.example.Backend.DTO.DTOS;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartDetailDTO {
    private Long cartDetailId;
    private Long courseId;
    private String title;
    private BigDecimal price;
    private String coverUrl;
}
