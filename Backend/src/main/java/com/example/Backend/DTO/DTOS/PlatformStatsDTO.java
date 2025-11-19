package com.example.Backend.DTO.DTOS;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PlatformStatsDTO {
    private final long userCount;
    private final long courseCount;
    private final double totalTransactionAmount;
    private final List<WeeklyStatsDTO> weeklyStats;

    @Getter
    @RequiredArgsConstructor
    public static class WeeklyStatsDTO {
        private final String weekLabel; // 例如: "10/21-10/27"
        private final long userCount; // 截至該周的累計用戶數
        private final long courseCount; // 截至該周的累計課程數
        private final double transactionAmount; // 該周的交易金額
        private final long newUsers; // 該周新增用戶數
        private final long newCourses; // 該周新增課程數
    }
}