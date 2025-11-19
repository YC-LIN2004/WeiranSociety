package com.example.Backend.DTO.DTOS;

import java.time.LocalDateTime;

public class CourseSectionDTO {

    private Long sectionId;
    private Long courseId;
    private String sectionTitle;
    private String sectionContent;
    private Integer sectionOrderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== Getter / Setter =====
    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionContent() {
        return sectionContent;
    }

    public void setSectionContent(String sectionContent) {
        this.sectionContent = sectionContent;
    }

    public Integer getSectionOrderIndex() {
        return sectionOrderIndex;
    }

    public void setSectionOrderIndex(Integer sectionOrderIndex) {
        this.sectionOrderIndex = sectionOrderIndex;
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
