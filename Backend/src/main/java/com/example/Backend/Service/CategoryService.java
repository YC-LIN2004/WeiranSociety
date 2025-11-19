package com.example.Backend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.DTO.Response.CategoryResponse;
import com.example.Backend.Entity.Category;
import com.example.Backend.Repository.CategoryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // 取得全部分類（含課程數量）
    public List<CategoryResponse> getAllCategories() {
        String jpql = """
                    SELECT new com.example.Backend.DTO.Response.CategoryResponse(
                        c.categoryId,
                        c.categoryName,
                        c.createdAt,
                        COUNT(course)
                    )
                    FROM Category c
                    LEFT JOIN c.courses course
                    GROUP BY c.categoryId, c.categoryName, c.createdAt
                    ORDER BY c.createdAt DESC
                """;

        TypedQuery<CategoryResponse> query = entityManager.createQuery(jpql, CategoryResponse.class);
        return query.getResultList();
    }

    // 依分類ID取得分類資料
    public Optional<CategoryResponse> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::convertToResponse);
    }

    // 模糊搜尋分類名稱
    public List<CategoryResponse> searchCategoryByName(String keyword) {
        List<Category> categories = categoryRepository.findByCategoryNameContainingIgnoreCase(keyword);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 建立新分類（含名稱重複檢查）
    public CategoryResponse createCategory(Category category) {
        boolean exists = categoryRepository
                .findByCategoryNameContainingIgnoreCase(category.getCategoryName())
                .stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase(category.getCategoryName()));

        if (exists) {
            throw new IllegalArgumentException("分類名稱已存在");
        }

        Category saved = categoryRepository.save(category);
        return convertToResponse(saved);
    }

    // 更新分類
    public CategoryResponse updateCategory(Long categoryId, Category newCategoryData) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("找不到指定的分類"));

        // 檢查是否名稱重複（非自己）
        boolean exists = categoryRepository
                .findByCategoryNameContainingIgnoreCase(newCategoryData.getCategoryName())
                .stream()
                .anyMatch(c -> !c.getCategoryId().equals(categoryId)
                        && c.getCategoryName().equalsIgnoreCase(newCategoryData.getCategoryName()));

        if (exists) {
            throw new IllegalArgumentException("分類名稱已存在");
        }

        category.setCategoryName(newCategoryData.getCategoryName());
        Category updated = categoryRepository.save(category);

        return convertToResponse(updated);
    }

    // 刪除分類
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("要刪除的分類不存在");
        }
        categoryRepository.deleteById(categoryId);
    }

    // 轉換成 Response
    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse res = new CategoryResponse();
        res.setCategoryId(category.getCategoryId());
        res.setCategoryName(category.getCategoryName());
        res.setCreatedAt(category.getCreatedAt());

        // 若關聯課程存在，計算數量
        if (category.getCourses() != null) {
            res.setTotalCourses((long) category.getCourses().size());
        } else {
            res.setTotalCourses(0L); //
        }

        return res;
    }
}
