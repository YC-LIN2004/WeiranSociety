package com.example.Backend.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.DTO.Response.TeacherProfileResponse;
import com.example.Backend.Entity.Course;
import com.example.Backend.Entity.TeacherProfile;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.TeacherProfileRepository;
import com.example.Backend.Repository.UsersRepository;
import com.example.Backend.Utils.TeacherStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class TeacherProfileService {

    @Autowired
    private TeacherProfileRepository teacherProfileRepository;

    @Autowired
    private UsersRepository usersRepository;

    // 取得所有老師（含使用者資料與課程）
    public List<TeacherProfileResponse> getAllTeachers() {
        return teacherProfileRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 依 teacherId 查詢老師
    public Optional<TeacherProfileResponse> getTeacherById(Long teacherId) {
        return teacherProfileRepository.findById(teacherId)
                .map(this::convertToResponse);
    }

    // 依 userId 查詢老師
    public TeacherProfileResponse getTeacherByUserId(Long userId) {
        TeacherProfile teacher = teacherProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("找不到 TeacherProfile，userId = " + userId));

        return convertToResponse(teacher);
    }

    // 依狀態查詢老師（ACTIVE / PENDING / SUSPENDED）
    public List<TeacherProfileResponse> getTeachersByStatus(String status) {
        TeacherStatus targetStatus;
        try {
            targetStatus = TeacherStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("無效的教師狀態：" + status);
        }

        return teacherProfileRepository.findByTeacherStatus(targetStatus).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 建立或更新老師資料
    public TeacherProfileResponse saveTeacherProfile(TeacherProfile teacherProfile) {
        // 檢查使用者是否存在
        if (teacherProfile.getUser() == null || teacherProfile.getUser().getUserID() == null) {
            throw new RuntimeException("申請失敗：缺少使用者資訊");
        }

        Long userId = teacherProfile.getUser().getUserID();
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到對應的使用者 ID = " + userId));

        // 綁定 Users 關聯
        teacherProfile.setUser(user);

        // 預設狀態
        if (teacherProfile.getTeacherStatus() == null) {
            teacherProfile.setTeacherStatus(TeacherStatus.PENDING);
        }

        teacherProfile.setUpdatedAt(LocalDateTime.now());

        TeacherProfile saved = teacherProfileRepository.save(teacherProfile);
        return convertToResponse(saved);
    }

    // 刪除老師資料
    public void deleteTeacher(Long teacherId) {
        teacherProfileRepository.deleteById(teacherId);
    }

    // 封裝轉換邏輯Response
    private TeacherProfileResponse convertToResponse(TeacherProfile teacher) {
        TeacherProfileResponse res = new TeacherProfileResponse();

        res.setTeacherId(teacher.getTeacherId());
        res.setBio(teacher.getBio());
        res.setExpertise(teacher.getExpertise());
        res.setTeacherRating(teacher.getTeacherRating());
        res.setTeacherStatus(
                teacher.getTeacherStatus() != null ? teacher.getTeacherStatus().name() : null);
        res.setCreatedAt(teacher.getCreatedAt());
        res.setUpdatedAt(teacher.getUpdatedAt());
        res.setCertificateUrl(teacher.getCertificateUrl());

        // 🔹 關聯使用者資訊（avatar / username / email）
        if (teacher.getUser() != null) {
            res.setUsername(teacher.getUser().getUsername());
            res.setAvatarUrl(teacher.getUser().getAvatar());
            res.setEmail(teacher.getUser().getEmail());
        }

        // 🔹 關聯課程（課程數量 + 簡易清單）
        if (teacher.getCourses() != null) {
            res.setTotalCourses(teacher.getCourses().size());
            List<String> courseTitles = teacher.getCourses().stream()
                    .map(Course::getCourseTitle)
                    .collect(Collectors.toList());
            res.setCourseTitles(courseTitles);
        }

        return res;
    }

    // 評分
    private final TeacherProfileRepository teacherRepo;

    public TeacherProfile rateTeacher(Long teacherId, Double newRating) {
        TeacherProfile teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("找不到老師"));

        // ✅ 安全防呆
        if (teacher.getTeacherRating() == null) {
            teacher.setTeacherRating(BigDecimal.ZERO);
        }
        if (teacher.getRatingCount() == null) {
            teacher.setRatingCount(0);
        }

        // ✅ 重新計算平均分數
        double currentTotal = teacher.getTeacherRating().doubleValue() * teacher.getRatingCount();
        int newCount = teacher.getRatingCount() + 1;
        double newAverage = (currentTotal + newRating) / newCount;

        teacher.setTeacherRating(BigDecimal.valueOf(newAverage));
        teacher.setRatingCount(newCount);

        return teacherRepo.save(teacher);
    }

    // 審核狀態更新
    public TeacherProfileResponse updateTeacherStatus(Long teacherId, TeacherStatus newStatus) {
        // 查詢教師
        TeacherProfile teacher = teacherProfileRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("找不到 TeacherProfile，ID = " + teacherId));

        // 確保關聯的使用者存在（避免 LazyInitializationException）
        if (teacher.getUser() == null) {
            // 這裡取 teacher.getUserId()
            throw new RuntimeException("TeacherProfile 缺少對應的 Users 關聯！");
        }

        // 更新狀態與時間
        teacher.setTeacherStatus(newStatus);
        teacher.setUpdatedAt(LocalDateTime.now());

        // 儲存更新
        teacherProfileRepository.save(teacher);

        // 回傳 DTO
        return convertToResponse(teacher);
    }

}
