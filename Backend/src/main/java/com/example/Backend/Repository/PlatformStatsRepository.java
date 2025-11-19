package com.example.Backend.Repository;

import org.springframework.stereotype.Repository;
import com.example.Backend.DTO.DTOS.PlatformStatsDTO;

@Repository
public interface PlatformStatsRepository {

    /**
     * 獲取平台基本統計數據（不含每周數據）
     */
    PlatformStatsDTO getPlatformStats();

    /**
     * 獲取平台統計數據（包含指定周數的每周數據）
     * 
     * @param weeksCount 要查詢的周數
     */
    PlatformStatsDTO getPlatformStatsWithWeekly(int weeksCount);
}