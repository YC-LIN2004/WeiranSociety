package com.example.Backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

    private String categoryName;

    // ===== Getter / Setter =====
    @NotBlank(message = "分類名稱不能為空")
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
