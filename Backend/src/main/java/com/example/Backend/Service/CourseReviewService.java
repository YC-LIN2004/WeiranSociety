package com.example.Backend.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Backend.Entity.CourseReview;
import com.example.Backend.Repository.CourseReviewRepository;

@Service
public class CourseReviewService {

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    // 查詢課程所有評論
    public List<CourseReview> getReviewsByCourse(Long courseId) {
        return courseReviewRepository.findByCourse_CourseIdOrderByCreatedAtDesc(courseId);
    }

    // 查詢使用者所有評論
    public List<CourseReview> getReviewsByUser(Long userId) {
        return courseReviewRepository.findByUser_UserId(userId);
    }

    // 新增或更新評論
    public CourseReview saveReview(CourseReview review) {
        return courseReviewRepository.save(review);
    }

    // 刪除評論
    public void deleteReview(Long reviewId) {
        courseReviewRepository.deleteById(reviewId);
    }
}
