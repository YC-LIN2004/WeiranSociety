package com.example.Backend.DTO.Request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeacherProfileRequest {

    @NotNull(message = "使用者 ID 不能為空")
    private Long userId;

    @Size(max = 500, message = "老師簡介長度不可超過 500 字")
    private String bio;

    @Size(max = 200, message = "專業領域長度不可超過 200 字")
    private String expertise;

    private BigDecimal teacherRating;

    private String teacherStatus;

    @NotBlank(message = "老師狀態不能為空")
    private String avatarUrl;

    // ===== Getter / Setter =====
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
