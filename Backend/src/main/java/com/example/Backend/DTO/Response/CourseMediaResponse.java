package com.example.Backend.DTO.Response;

import java.time.LocalDateTime;

public class CourseMediaResponse {

    private Long courseMediaId;
    private Long courseId;
    private Long sectionId;
    private String mediaUrl;
    private String mediaTitle;
    private Integer mediaOrderIndex;
    private LocalDateTime createdAt;

    // 額外顯示用欄位（非資料表欄位）
    private String courseTitle;

    // ===== Constructor for JPQL Projection =====
    public CourseMediaResponse(Long courseMediaId, Long courseId, Long sectionId,
            String mediaUrl, String mediaTitle,
            Integer mediaOrderIndex, LocalDateTime createdAt,
            String courseTitle) {
        this.courseMediaId = courseMediaId;
        this.courseId = courseId;
        this.sectionId = sectionId;
        this.mediaUrl = mediaUrl;
        this.mediaTitle = mediaTitle;
        this.mediaOrderIndex = mediaOrderIndex;
        this.createdAt = createdAt;
        this.courseTitle = courseTitle;
    }

    // ===== Default Constructor =====
    public CourseMediaResponse() {
    }

    // ===== Getter / Setter =====
    public Long getCourseMediaId() {
        return courseMediaId;
    }

    public void setCourseMediaId(Long courseMediaId) {
        this.courseMediaId = courseMediaId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public Integer getMediaOrderIndex() {
        return mediaOrderIndex;
    }

    public void setMediaOrderIndex(Integer mediaOrderIndex) {
        this.mediaOrderIndex = mediaOrderIndex;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
}
