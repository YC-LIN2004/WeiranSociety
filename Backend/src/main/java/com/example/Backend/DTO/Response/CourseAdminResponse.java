package com.example.Backend.DTO.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseAdminResponse {
    private Long courseId;
    private String courseTitle;
    private Long categoryId;
    private String categoryName;
    private Long teacherId;
    private String teacherName;
    private BigDecimal price;
    private String courseStatus;
    private LocalDateTime createdAt;

    // ✅ 新增封面欄位
    private String coverUrl;

    // ✅ 新增新的建構子，保留舊的也行
    public CourseAdminResponse(Long courseId, String courseTitle, Long categoryId, String categoryName,
            Long teacherId, String teacherName, BigDecimal price, String courseStatus,
            LocalDateTime createdAt, String coverUrl) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.price = price;
        this.courseStatus = courseStatus;
        this.createdAt = createdAt;
        this.coverUrl = coverUrl;
    }

    // ===== Getter =====
    public Long getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCoverUrl() {
        return coverUrl;
    } // ✅ 新增
}
