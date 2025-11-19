package com.example.Backend.DTO.Request;

import java.math.BigDecimal;

public class CourseReviewRequest {

    private Long courseId;
    private Long userId;
    private BigDecimal courseRating;
    private String comment;

    // ===== Getter / Setter =====
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(BigDecimal courseRating) {
        this.courseRating = courseRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
