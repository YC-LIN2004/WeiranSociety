package com.example.Backend.DTO.Request;

import java.math.BigDecimal;
import java.util.List;

public class CourseUpdateRequest {
    private Long teacherId;
    private String courseTitle;
    private String categoryName;
    private String courseDescription;
    private BigDecimal price;
    private String coverUrl;
    private List<SectionDTO> sections;

    // ✅ Section DTO
    public static class SectionDTO {
        private String title;
        private List<VideoDTO> videos;

        public static class VideoDTO {
            private String title;
            private String url;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<VideoDTO> getVideos() {
            return videos;
        }

        public void setVideos(List<VideoDTO> videos) {
            this.videos = videos;
        }
    }

    // ✅ Getter / Setter
    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public List<SectionDTO> getSections() {
        return sections;
    }

    public void setSections(List<SectionDTO> sections) {
        this.sections = sections;
    }
}
