package com.example.Backend.DTO.DTOS;

public record OrderTabCountDTO(
                long completed,
                long unpaid,
                long cancelled) {
}
