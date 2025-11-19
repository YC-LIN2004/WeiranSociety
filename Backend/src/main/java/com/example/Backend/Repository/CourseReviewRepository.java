package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.CourseReview;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    // 查詢某課程所有評論
    List<CourseReview> findByCourse_CourseIdOrderByCreatedAtDesc(Long courseId);

    // 查詢某使用者發表過的所有評論
    List<CourseReview> findByUser_UserId(Long userId);
}
