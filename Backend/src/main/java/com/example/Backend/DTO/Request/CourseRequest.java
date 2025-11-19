package com.example.Backend.DTO.Request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourseRequest {

    // 課程分類名稱（例如 "程式開發"）
    @NotBlank(message = "分類名稱不能為空")
    @JsonProperty("category")
    @JsonAlias({ "categoryName" })
    private String category;

    // 老師 ID
    @NotNull(message = "老師 ID 不能為空")
    private Long teacherId;

    // 課程標題
    @NotBlank(message = "課程標題不能為空")
    private String courseTitle;

    // 價格
    @DecimalMin(value = "0.0", inclusive = false, message = "價格必須大於 0")
    private BigDecimal price;

    // 課程描述
    @NotBlank(message = "課程描述不能為空")
    private String description;

    // 封面圖片
    @NotBlank(message = "封面圖片 URL 不能為空")
    private String coverUrl;

    // 狀態（例如 ACTIVE / DRAFT）
    private String courseStatus;

    // 所有章節（每個章節裡包含多支影片）
    private List<CourseSectionRequest> sections;

    // ===== Getter / Setter =====

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryName() {
        return category;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    public List<CourseSectionRequest> getSections() {
        return sections;
    }

    public void setSections(List<CourseSectionRequest> sections) {
        this.sections = sections;
    }
}
