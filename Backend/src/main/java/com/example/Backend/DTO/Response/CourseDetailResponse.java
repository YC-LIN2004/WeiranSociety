package com.example.Backend.DTO.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CourseDetailResponse {

    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private BigDecimal price;
    private String coverUrl;
    private String categoryName;
    private LocalDateTime createdAt;
    private List<SectionInfo> sections;

    // ✅ 新增老師資訊
    private TeacherInfo teacher;

    // ======== 章節 SectionInfo ========
    public static class SectionInfo {
        private String sectionTitle;
        private List<VideoInfo> videos;

        public SectionInfo(String sectionTitle, List<VideoInfo> videos) {
            this.sectionTitle = sectionTitle;
            this.videos = videos;
        }

        public String getSectionTitle() {
            return sectionTitle;
        }

        public List<VideoInfo> getVideos() {
            return videos;
        }

        // ✅ 影片資訊子物件
        public static class VideoInfo {
            private String videoTitle;
            private String videoUrl;

            public VideoInfo(String videoTitle, String videoUrl) {
                this.videoTitle = videoTitle;
                this.videoUrl = videoUrl;
            }

            public String getVideoTitle() {
                return videoTitle;
            }

            public String getVideoUrl() {
                return videoUrl;
            }
        }
    }

    public static class TeacherInfo {
        private Long teacherId;
        private String name;
        private String avatarUrl;
        private BigDecimal rating;
        private String bio;
        private String expertise;

        public TeacherInfo() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public BigDecimal getRating() {
            return rating;
        }

        public void setRating(BigDecimal rating) {
            this.rating = rating;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getExpertise() {
            return expertise;
        }

        public void setExpertise(String expertise) {
            this.expertise = expertise;
        }

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }
    }

    // ======== Getter / Setter ========

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

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<SectionInfo> getSections() {
        return sections;
    }

    public void setSections(List<SectionInfo> sections) {
        this.sections = sections;
    }

    public TeacherInfo getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherInfo teacher) {
        this.teacher = teacher;
    }
}
