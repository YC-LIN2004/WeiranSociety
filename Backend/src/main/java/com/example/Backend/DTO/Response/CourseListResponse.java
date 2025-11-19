package com.example.Backend.DTO.Response;

import java.math.BigDecimal;
import com.example.Backend.Entity.Course;

public class CourseListResponse {

        private Long courseId;
        private String courseTitle;
        private BigDecimal price;
        private String coverUrl;
        private String categoryName;
        private String teacherName;
        private String teacherAvatarUrl;

        // ===== fromEntity() =====
        public static CourseListResponse fromEntity(Course course) {
                if (course == null)
                        return null;

                String categoryName = null;
                if (course.getCategory() != null) {
                        categoryName = course.getCategory().getCategoryName();
                }

                String teacherName = null;
                String teacherAvatarUrl = null;
                if (course.getTeacher() != null && course.getTeacher().getUser() != null) {
                        teacherName = course.getTeacher().getUser().getUsername();
                        String avatarPath = course.getTeacher().getUser().getAvatar();
                        if (avatarPath != null) {
                                // ✅ 組完整路徑
                                teacherAvatarUrl = "http://localhost:8080/api" + avatarPath;
                        }
                }

                CourseListResponse dto = new CourseListResponse();
                dto.setCourseId(course.getCourseId());
                dto.setCourseTitle(course.getCourseTitle());
                dto.setPrice(course.getPrice());
                dto.setCoverUrl(course.getCoverUrl());
                dto.setCategoryName(categoryName);
                dto.setTeacherName(teacherName); // ✅ 放老師名字
                dto.setTeacherAvatarUrl(teacherAvatarUrl); // ✅ 放老師頭像
                return dto;
        }

        // ===== Getter / Setter =====
        public Long getCourseId() {
                return courseId;
        }

        public void setCourseId(Long courseId) {
                this.courseId = courseId;
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

        public String getCoverUrl() {
                return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
                this.coverUrl = coverUrl;
        }

        public String getCategoryName() {
                return categoryName;
        }

        public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
        }

        public String getTeacherName() {
                return teacherName;
        }

        public void setTeacherName(String teacherName) {
                this.teacherName = teacherName;
        }

        public String getTeacherAvatarUrl() {
                return teacherAvatarUrl;
        }

        public void setTeacherAvatarUrl(String teacherAvatarUrl) {
                this.teacherAvatarUrl = teacherAvatarUrl;
        }
}
