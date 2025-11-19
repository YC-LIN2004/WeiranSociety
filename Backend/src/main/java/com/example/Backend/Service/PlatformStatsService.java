package com.example.Backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Backend.DTO.DTOS.PlatformStatsDTO;
import com.example.Backend.Repository.PlatformStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlatformStatsService {

    private final PlatformStatsRepository platformStatsRepository;

    /**
     * 獲取平台基本統計數據
     */
    public PlatformStatsDTO getPlatformStats() {
        return platformStatsRepository.getPlatformStats();
    }

    /**
     * 獲取平台統計數據（含每周折線圖數據）
     * 
     * @param weeksCount 要查詢的周數，默認為 8 周
     */
    public PlatformStatsDTO getPlatformStatsWithWeekly(Integer weeksCount) {
        // 設定默認值和範圍限制
        if (weeksCount == null || weeksCount < 1) {
            weeksCount = 8;
        }
        if (weeksCount > 52) {
            weeksCount = 52; // 最多查詢一年
        }

        return platformStatsRepository.getPlatformStatsWithWeekly(weeksCount);
    }
}