package com.example.Backend.DTO.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TeacherProfileResponse {

    private Long teacherId;
    private String username;
    private String email;
    private String avatarUrl;
    private String bio;
    private String expertise;
    private BigDecimal teacherRating;
    private String teacherStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String certificateUrl;

    // 額外顯示資訊
    private Integer totalCourses;
    private List<String> courseTitles;

    public TeacherProfileResponse() {
    }

    public TeacherProfileResponse(Long teacherId, String username, String email, String avatarUrl,
            String bio, String expertise, BigDecimal teacherRating,
            String teacherStatus, LocalDateTime createdAt, Integer totalCourses) {
        this.teacherId = teacherId;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.expertise = expertise;
        this.teacherRating = teacherRating;
        this.teacherStatus = teacherStatus;
        this.createdAt = createdAt;
        this.totalCourses = totalCourses;
    }

    // ===== Getter / Setter =====

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public BigDecimal getTeacherRating() {
        return teacherRating;
    }

    public void setTeacherRating(BigDecimal teacherRating) {
        this.teacherRating = teacherRating;
    }

    public String getTeacherStatus() {
        return teacherStatus;
    }

    public void setTeacherStatus(String teacherStatus) {
        this.teacherStatus = teacherStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Integer totalCourses) {
        this.totalCourses = totalCourses;
    }

    public List<String> getCourseTitles() {
        return courseTitles;
    }

    public void setCourseTitles(List<String> courseTitles) {
        this.courseTitles = courseTitles;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
