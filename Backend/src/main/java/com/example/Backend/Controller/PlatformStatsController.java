package com.example.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.DTO.DTOS.PlatformStatsDTO;
import com.example.Backend.Service.PlatformStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 根據需要調整 CORS 設定
public class PlatformStatsController {

    private final PlatformStatsService platformStatsService;

    /**
     * 獲取平台基本統計數據
     * GET /api/stats/platform
     */
    @GetMapping("/platform")
    public ResponseEntity<PlatformStatsDTO> getPlatformStats() {
        PlatformStatsDTO stats = platformStatsService.getPlatformStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 獲取平台統計數據（含每周折線圖數據）
     * GET /api/stats/platform/weekly?weeks=12
     * 
     * @param weeks 要查詢的周數（默認 8 周，最多 52 周）
     */
    @GetMapping("/platform/weekly")
    public ResponseEntity<PlatformStatsDTO> getPlatformStatsWithWeekly(
            @RequestParam(name = "weeks", required = false, defaultValue = "8") Integer weeks) {
        PlatformStatsDTO stats = platformStatsService.getPlatformStatsWithWeekly(weeks);
        return ResponseEntity.ok(stats);
    }
}