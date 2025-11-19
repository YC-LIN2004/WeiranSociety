package com.example.Backend.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.Backend.Entity.CourseReview;
import com.example.Backend.Service.CourseReviewService;

@RestController
@RequestMapping("/api/course-review")
@CrossOrigin(origins = "*")
public class CourseReviewController {

    @Autowired
    private CourseReviewService courseReviewService;

    // 查詢課程所有評論
    @GetMapping("/course/{courseId}")
    public List<CourseReview> getReviewsByCourse(@PathVariable Long courseId) {
        return courseReviewService.getReviewsByCourse(courseId);
    }

    // 查詢使用者所有評論
    @GetMapping("/user/{userId}")
    public List<CourseReview> getReviewsByUser(@PathVariable Long userId) {
        return courseReviewService.getReviewsByUser(userId);
    }

    // 新增或更新評論
    @PostMapping
    public CourseReview saveReview(@RequestBody CourseReview review) {
        return courseReviewService.saveReview(review);
    }

    // 刪除評論
    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        courseReviewService.deleteReview(reviewId);
    }
}
