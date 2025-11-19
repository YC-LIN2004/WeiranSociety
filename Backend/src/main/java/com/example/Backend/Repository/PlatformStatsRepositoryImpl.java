package com.example.Backend.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.Backend.DTO.DTOS.PlatformStatsDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class PlatformStatsRepositoryImpl implements PlatformStatsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PlatformStatsDTO getPlatformStats() {
        Object[] result = (Object[]) entityManager.createNativeQuery("""
                    SELECT
                        (SELECT COUNT(*) FROM Users),
                        (SELECT COUNT(*) FROM Course),
                        (SELECT ISNULL(SUM(TotalAmount), 0) FROM UserOrder)
                """).getSingleResult();

        long userCount = ((Number) result[0]).longValue();
        long courseCount = ((Number) result[1]).longValue();
        double totalAmount = ((Number) result[2]).doubleValue();

        return new PlatformStatsDTO(userCount, courseCount, totalAmount, new ArrayList<>());
    }

    @Override
    public PlatformStatsDTO getPlatformStatsWithWeekly(int weeksCount) {
        // 獲取基本統計
        Object[] basicResult = (Object[]) entityManager.createNativeQuery("""
                    SELECT
                        (SELECT COUNT(*) FROM Users),
                        (SELECT COUNT(*) FROM Course),
                        (SELECT ISNULL(SUM(TotalAmount), 0) FROM UserOrder)
                """).getSingleResult();

        long userCount = ((Number) basicResult[0]).longValue();
        long courseCount = ((Number) basicResult[1]).longValue();
        double totalAmount = ((Number) basicResult[2]).doubleValue();

        // 計算日期範圍
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(weeksCount - 1).with(DayOfWeek.MONDAY);

        // 🔧 修正：將 [Order] 改為 UserOrder
        String sql = """
                WITH WeekNumbers AS (
                    SELECT
                        DATEADD(WEEK, number, ?) AS WeekStart,
                        DATEADD(DAY, 6, DATEADD(WEEK, number, ?)) AS WeekEnd
                    FROM master..spt_values
                    WHERE type = 'P' AND number BETWEEN 0 AND ?
                )
                SELECT
                    FORMAT(wn.WeekStart, 'MM/dd') + '-' + FORMAT(wn.WeekEnd, 'MM/dd') AS WeekLabel,
                    (SELECT COUNT(*) FROM Users WHERE CreatedAt <= DATEADD(DAY, 1, wn.WeekEnd)) AS UserCount,
                    (SELECT COUNT(*) FROM Course WHERE CreatedAt <= DATEADD(DAY, 1, wn.WeekEnd)) AS CourseCount,
                    -- ✅ 修正：直接在 UserOrder 計算交易金額
                    ISNULL((
                        SELECT SUM(uo.TotalAmount)
                        FROM UserOrder uo
                        WHERE uo.CreatedAt >= wn.WeekStart
                          AND uo.CreatedAt < DATEADD(DAY, 1, wn.WeekEnd)
                    ), 0) AS TransactionAmount,
                    (SELECT COUNT(*) FROM Users
                     WHERE CreatedAt >= wn.WeekStart AND CreatedAt < DATEADD(DAY, 1, wn.WeekEnd)) AS NewUsers,
                    (SELECT COUNT(*) FROM Course
                     WHERE CreatedAt >= wn.WeekStart AND CreatedAt < DATEADD(DAY, 1, wn.WeekEnd)) AS NewCourses
                FROM WeekNumbers wn
                ORDER BY wn.WeekStart
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> weekResults = entityManager.createNativeQuery(sql)
                .setParameter(1, startDate.atStartOfDay())
                .setParameter(2, startDate.atStartOfDay())
                .setParameter(3, weeksCount - 1)
                .getResultList();

        // 轉換為 DTO
        List<PlatformStatsDTO.WeeklyStatsDTO> weeklyStats = new ArrayList<>();
        for (Object[] row : weekResults) {
            weeklyStats.add(new PlatformStatsDTO.WeeklyStatsDTO(
                    (String) row[0], // weekLabel
                    ((Number) row[1]).longValue(), // userCount
                    ((Number) row[2]).longValue(), // courseCount
                    ((Number) row[3]).doubleValue(), // transactionAmount
                    ((Number) row[4]).longValue(), // newUsers
                    ((Number) row[5]).longValue() // newCourses
            ));
        }

        return new PlatformStatsDTO(userCount, courseCount, totalAmount, weeklyStats);
    }
}