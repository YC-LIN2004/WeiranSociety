package com.example.Backend.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.DTO.Response.CategoryResponse;
import com.example.Backend.Entity.Category;
import com.example.Backend.Service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getAllCategoriesSimple() {
        return categoryService.getAllCategories();
    }

    // 取得全部分類（含課程數量）
    @GetMapping("/all")
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // 依ID查詢分類
    @GetMapping("/{categoryId}")
    public Optional<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    // 模糊搜尋分類名稱
    @GetMapping("/search")
    public List<CategoryResponse> searchCategoryByName(
            @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return categoryService.getAllCategories();
        }
        return categoryService.searchCategoryByName(keyword);
    }

    // 新增分類
    @PostMapping("/create")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody Category request) {
        CategoryResponse created = categoryService.createCategory(request);
        return ResponseEntity.ok(created);
    }

    // 更新分類
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody Category request) {
        CategoryResponse updated = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(updated);
    }

    // 刪除分類
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(Map.of("message", "分類刪除成功"));
    }

}
