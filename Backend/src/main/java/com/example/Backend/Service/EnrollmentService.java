package com.example.Backend.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.DTO.Request.EnrollmentRequest;
import com.example.Backend.DTO.Response.EnrollmentResponse;
import com.example.Backend.Entity.Course;
import com.example.Backend.Entity.Enrollment;
import com.example.Backend.Entity.StudentProfile;
import com.example.Backend.Repository.CourseRepository;
import com.example.Backend.Repository.EnrollmentRepository;
import com.example.Backend.Repository.StudentProfileRepository;
import com.example.Backend.Utils.EnrollmentStatus;

import jakarta.transaction.Transactional;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    // 依 UserId 查詢修課紀錄
    public List<EnrollmentResponse> getEnrollmentByUserId(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_Users_UserId(userId);
        return enrollments.stream()
                .map(e -> {
                    EnrollmentResponse res = new EnrollmentResponse();
                    res.setEnrollmentId(e.getEnrollmentId());
                    res.setCourseId(e.getCourse().getCourseId());
                    res.setCourseTitle(e.getCourse().getCourseTitle());
                    res.setCoverUrl(e.getCourse().getCoverUrl());
                    res.setProgress(e.getProgress());
                    res.setEnrollmentStatus(e.getEnrollmentStatus().name());

                    if (e.getCourse().getTeacher() != null &&
                            e.getCourse().getTeacher().getUser() != null) {
                        res.setTeacherName(e.getCourse().getTeacher().getUser().getUsername());
                    }

                    return res;
                })
                .collect(Collectors.toList());
    }

    // 新增修課紀錄（學生購課後）
    public EnrollmentResponse addEnrollment(EnrollmentRequest req) {
        // 取得學生資料
        StudentProfile student = studentProfileRepository.findByUsers_UserId(req.getUserId())
                .orElseThrow(() -> new RuntimeException("找不到學生資料，userId=" + req.getUserId()));

        // 取得課程
        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("找不到課程，courseId=" + req.getCourseId()));

        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        e.setProgress(req.getProgress() != null ? req.getProgress() : 0.0);
        e.setEnrollmentStatus(EnrollmentStatus.ENROLLMENTPENDING);

        Enrollment saved = enrollmentRepository.save(e);
        return toResponse(saved);
    }

    // 更新課程進度（自動調整狀態）
    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, Double progress) {
        Enrollment e = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("修課紀錄不存在，enrollmentId=" +
                        enrollmentId));

        if (e.getEnrollmentStatus() == EnrollmentStatus.CANCELLED)
            return toResponse(e);

        double safeProgress = progress == null ? 0.0
                : Math.max(0.0, Math.min(100.0,
                        progress));
        e.setProgress(safeProgress);

        if (safeProgress >= 100.0)
            e.setEnrollmentStatus(EnrollmentStatus.COMPLETED);
        else if (safeProgress > 0.0)
            e.setEnrollmentStatus(EnrollmentStatus.ONGOING);
        else
            e.setEnrollmentStatus(EnrollmentStatus.ENROLLMENTPENDING);

        return toResponse(enrollmentRepository.save(e));
    }

    // 退課
    public EnrollmentResponse cancelEnrollment(Long enrollmentId) {
        Enrollment e = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("修課紀錄不存在，enrollmentId=" +
                        enrollmentId));

        if (e.getEnrollmentStatus() == EnrollmentStatus.CANCELLED)
            return toResponse(e);

        if (e.getEnrollmentStatus() == EnrollmentStatus.COMPLETED)
            throw new IllegalStateException("課程已完成，無法退課");

        e.setEnrollmentStatus(EnrollmentStatus.CANCELLED);
        return toResponse(enrollmentRepository.save(e));
    }

    // Entity → Response
    private EnrollmentResponse

            toResponse(Enrollment e) {
        EnrollmentResponse res = new EnrollmentResponse();
        res.setEnrollmentId(e.getEnrollmentId());

        // ✅ 由 Enrollment → StudentProfile → Users 取得 userId
        if (e.getStudent() != null && e.getStudent().getUsers() != null) {
            res.setUserId(e.getStudent().getUsers().getUserID());
        }

        // ✅ 課程相關資料
        if (e.getCourse() != null) {
            res.setCourseId(e.getCourse().getCourseId());
            res.setCourseTitle(e.getCourse().getCourseTitle());

            if (e.getCourse().getTeacher() != null &&
                    e.getCourse().getTeacher().getUser() != null) {
                res.setTeacherName(e.getCourse().getTeacher().getUser().getUsername());
            }
        }

        // ✅ 其他欄位
        res.setProgress(e.getProgress());
        res.setEnrollmentStatus(e.getEnrollmentStatus().name());
        res.setCreatedAt(e.getCreatedAt());
        res.setUpdatedAt(e.getUpdatedAt());

        return res;
    }
}