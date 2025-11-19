package com.example.Backend.DTO.Request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class CourseSectionRequest {

    @NotBlank(message = "章節標題不能為空")
    private String sectionTitle;

    private String sectionContent;

    private Integer sectionOrderIndex;

    private List<CourseMediaRequest> videos;
    // ===== Getter / Setter =====

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

    public List<CourseMediaRequest> getVideos() {
        return videos;
    }

    public void setVideos(List<CourseMediaRequest> videos) {
        this.videos = videos;
    }

}
