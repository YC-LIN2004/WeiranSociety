package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.Backend.Entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 模糊搜尋分類名稱
    List<Category> findByCategoryNameContainingIgnoreCase(String keyword);

    // 根據建立時間排序
    List<Category> findAllByOrderByCreatedAtDesc();

    // 查詢是否存在相同分類名稱（避免重複）
    boolean existsByCategoryNameIgnoreCase(String categoryName);

    // 精確查詢
    Category findByCategoryName(String categoryName);
}
