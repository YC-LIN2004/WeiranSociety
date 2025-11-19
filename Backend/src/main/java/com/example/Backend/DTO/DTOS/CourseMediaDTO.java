package com.example.Backend.DTO.DTOS;

import java.time.LocalDateTime;

public class CourseMediaDTO {

    private Long courseMediaId;
    private Long courseId;
    private Long sectionId;
    private String mediaUrl;
    private String mediaTitle;
    private Integer mediaOrderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
