package com.example.Backend.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "CourseMedia")
public class CourseMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseMediaId;

    @Column(length = 100)
    private String mediaTitle;

    @Column(columnDefinition = "nvarchar(max)")
    private String mediaUrl;

    @Column(nullable = false)
    private Integer mediaOrderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SectionId", nullable = false)
    @JsonIgnoreProperties("courseMedias")
    private CourseSection section;

    @Column(name = "CreatedAt", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // ===== Getter / Setter =====
    public Long getCourseMediaId() {
        return courseMediaId;
    }

    public void setCourseMediaId(Long courseMediaId) {
        this.courseMediaId = courseMediaId;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Integer getMediaOrderIndex() {
        return mediaOrderIndex;
    }

    public void setMediaOrderIndex(Integer mediaOrderIndex) {
        this.mediaOrderIndex = mediaOrderIndex;
    }

    public CourseSection getSection() {
        return section;
    }

    public void setSection(CourseSection section) {
        this.section = section;
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
