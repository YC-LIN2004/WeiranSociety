package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.example.Backend.Entity.TeacherProfile;
import com.example.Backend.Utils.TeacherStatus;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    // 依使用者 ID 查詢老師資料
    Optional<TeacherProfile> findByUser_UserId(Long userId);

    // 依老師狀態查詢（ACTIVE / PENDING / SUSPENDED / REJECTED）
    List<TeacherProfile> findByTeacherStatus(TeacherStatus teacherStatus);

    // 模糊搜尋老師姓名
    List<TeacherProfile> findByUser_UsernameContainingIgnoreCase(String keyword);

    // 後台顯示最新申請的老師資料
    List<TeacherProfile> findByTeacherStatusOrderByCreatedAtDesc(TeacherStatus status);

    // 統計各狀態老師數量
    long countByTeacherStatus(TeacherStatus status);

    // 精確查詢
    TeacherProfile findByTeacherId(Long teacherId);

}
