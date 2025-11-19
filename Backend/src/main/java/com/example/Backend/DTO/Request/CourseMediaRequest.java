package com.example.Backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;

public class CourseMediaRequest {

    // 章節 ID（僅在單獨上傳時使用，建立課程時不需）
    private Long sectionId;

    // 課程 ID（僅在單獨上傳時使用）
    private Long courseId;

    @NotBlank(message = "影片連結不能為空")
    private String url;

    @NotBlank(message = "影片標題不能為空")
    private String title;

    private Integer mediaOrderIndex;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getMediaOrderIndex() {
        return mediaOrderIndex;
    }

    public void setMediaOrderIndex(Integer mediaOrderIndex) {
        this.mediaOrderIndex = mediaOrderIndex;
    }
}
