package com.example.Backend.DTO.Response;

import java.time.LocalDateTime;

public class CategoryResponse {

    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private Long totalCourses; // ✅ 對應 COUNT(course)

    public CategoryResponse() {
    }

    // ✅ JPQL 建構子（對應 CategoryService 的 SELECT new ...）
    public CategoryResponse(Long categoryId, String categoryName, LocalDateTime createdAt, Long totalCourses) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
        this.totalCourses = totalCourses;
    }

    // Getter / Setter
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
    }
}
