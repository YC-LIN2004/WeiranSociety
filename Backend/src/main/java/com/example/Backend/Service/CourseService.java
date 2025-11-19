package com.example.Backend.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.DTO.Request.CourseMediaRequest;
import com.example.Backend.DTO.Request.CourseRequest;
import com.example.Backend.DTO.Request.CourseSectionRequest;
import com.example.Backend.DTO.Request.CourseUpdateRequest;
import com.example.Backend.DTO.Response.CourseDetailResponse;
import com.example.Backend.Entity.Category;
import com.example.Backend.Entity.Course;
import com.example.Backend.Entity.CourseMedia;
import com.example.Backend.Entity.CourseSection;
import com.example.Backend.Entity.TeacherProfile;
import com.example.Backend.Repository.CategoryRepository;
import com.example.Backend.Repository.CourseMediaRepository;
import com.example.Backend.Repository.CourseRepository;
import com.example.Backend.Repository.CourseSectionRepository;
import com.example.Backend.Repository.TeacherProfileRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private CourseMediaRepository courseMediaRepository;

    @Autowired
    private TeacherProfileRepository teacherProfileRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // 取得全部課程
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // 依 ID 查課程
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    // 依老師 ID 查課程
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    // 模糊搜尋課程（名稱 / 關鍵字）
    public List<Course> searchCoursesByKeyword(String keyword) {
        return courseRepository.findByCourseTitleContainingIgnoreCase(keyword);
    }

    // 建立新課程
    public Course createCourse(Course dto) {
        Course course = new Course();
        course.setCourseTitle(dto.getCourseTitle());
        course.setCourseDescription(dto.getCourseDescription());
        course.setPrice(dto.getPrice());
        course.setCoverUrl(dto.getCoverUrl());

        // 查關聯對象
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("分類不存在"));
        TeacherProfile teacher = teacherProfileRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("老師不存在"));

        course.setCategory(category);
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    // 更新課程
    @Transactional
    public void updateCourse(Long courseId, CourseUpdateRequest req) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("找不到課程 ID: " + courseId));

        // === 基本資料 ===
        course.setCourseTitle(req.getCourseTitle());
        course.setCourseDescription(req.getCourseDescription());
        course.setPrice(req.getPrice());
        course.setCoverUrl(req.getCoverUrl());
        course.setUpdatedAt(LocalDateTime.now());

        // === 更新分類 ===
        if (req.getCategoryName() != null) {
            Category category = categoryRepository.findByCategoryName(req.getCategoryName());
            if (category == null) {
                category = new Category();
                category.setCategoryName(req.getCategoryName());
                categoryRepository.save(category);
            }
            course.setCategory(category);
        }

        courseRepository.save(course);

        // === 刪除舊章節與影片 ===
        List<CourseSection> oldSections = courseSectionRepository.findByCourse(course);
        for (CourseSection s : oldSections) {
            courseMediaRepository.deleteAll(s.getCourseMedias());
        }
        courseSectionRepository.deleteAll(oldSections);

        // === 重新建立章節與影片 ===
        if (req.getSections() != null) {
            for (int i = 0; i < req.getSections().size(); i++) {
                var sDto = req.getSections().get(i);
                CourseSection section = new CourseSection();
                section.setCourse(course);
                section.setSectionTitle(sDto.getTitle());
                section.setSectionOrderIndex(i + 1);
                courseSectionRepository.save(section);

                if (sDto.getVideos() != null) {
                    for (int j = 0; j < sDto.getVideos().size(); j++) {
                        var vDto = sDto.getVideos().get(j);
                        CourseMedia media = new CourseMedia();
                        media.setSection(section);
                        media.setMediaTitle(vDto.getTitle());
                        media.setMediaUrl(vDto.getUrl());
                        media.setMediaOrderIndex(j + 1);
                        courseMediaRepository.save(media);
                    }
                }
            }
        }
    }

    // 刪除課程
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("課程不存在");
        }
        courseRepository.deleteById(courseId);
    }

    // 整合邏輯區（老師上傳課程：一次建立五表）
    @Transactional
    public Course createCourseWithSections(CourseRequest req) {

        // 驗證分類
        String categoryName = req.getCategory();
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("課程分類不可為空！");
        }

        // 找或建立分類
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category == null) {
            category = new Category();
            category.setCategoryName(categoryName);
            category = categoryRepository.save(category);
        }

        // 找老師
        TeacherProfile teacher = teacherProfileRepository.findById(req.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("找不到老師！"));

        // 建立課程
        Course course = new Course();
        course.setTeacher(teacher);
        course.setTeacherId(teacher.getTeacherId());
        course.setCourseTitle(req.getCourseTitle());
        course.setCourseDescription(req.getDescription());
        course.setPrice(req.getPrice());
        course.setCoverUrl(req.getCoverUrl());
        course.setCourseStatus("ACTIVE");
        course.setCategory(category);
        course.setCategoryId(category.getCategoryId());

        // 儲存課程以產生 courseId
        course = courseRepository.saveAndFlush(course);
        if (category == null)
            throw new IllegalArgumentException("課程分類不存在或已被刪除！");
        // 建立章節與影片
        if (req.getSections() != null && !req.getSections().isEmpty()) {
            int orderIndex = 1;

            for (CourseSectionRequest s : req.getSections()) {
                CourseSection section = new CourseSection();
                section.setCourse(course);
                section.setSectionTitle(s.getSectionTitle());
                section.setSectionContent(s.getSectionContent());
                section.setSectionOrderIndex(
                        s.getSectionOrderIndex() != null ? s.getSectionOrderIndex() : orderIndex++);

                // 儲存章節
                CourseSection savedSection = courseSectionRepository.saveAndFlush(section);

                // 建立影片
                List<CourseMediaRequest> videos = s.getVideos();
                if (videos != null && !videos.isEmpty()) {
                    int mediaOrder = 1;
                    for (CourseMediaRequest v : videos) {
                        CourseMedia media = new CourseMedia();
                        media.setSection(savedSection);
                        media.setMediaTitle(v.getTitle());
                        media.setMediaUrl(v.getUrl());
                        media.setMediaOrderIndex(mediaOrder++);
                        courseMediaRepository.save(media);
                    }
                }
            }
        }

        return course;
    }

    // 管理課程
    public CourseDetailResponse getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("找不到課程 ID: " + courseId));

        CourseDetailResponse response = new CourseDetailResponse();
        response.setCourseId(course.getCourseId());
        response.setCourseTitle(course.getCourseTitle());
        response.setCourseDescription(course.getCourseDescription());
        response.setPrice(course.getPrice());
        response.setCoverUrl(course.getCoverUrl());
        response.setCreatedAt(course.getCreatedAt());

        if (course.getCategory() != null) {
            response.setCategoryName(course.getCategory().getCategoryName());
        }

        // 🧩 老師資訊
        if (course.getTeacher() != null) {
            var teacher = course.getTeacher();
            var teacherUser = teacher.getUser();

            CourseDetailResponse.TeacherInfo t = new CourseDetailResponse.TeacherInfo();
            t.setTeacherId(teacher.getTeacherId());
            t.setName(teacherUser != null ? teacherUser.getUsername() : "未提供姓名");
            t.setAvatarUrl(teacherUser != null ? teacherUser.getAvatar() : null);
            t.setBio(teacher.getBio());
            t.setExpertise(teacher.getExpertise());
            t.setRating(teacher.getTeacherRating());

            response.setTeacher(t);
        }

        // 🧩 章節 + 影片（CourseSection -> CourseMedia）
        if (course.getSections() != null && !course.getSections().isEmpty()) {
            List<CourseDetailResponse.SectionInfo> sections = course.getSections().stream()
                    .map(sec -> {
                        // 處理每個章節下的影片
                        List<CourseDetailResponse.SectionInfo.VideoInfo> videos = new ArrayList<>();

                        if (sec.getCourseMedias() != null && !sec.getCourseMedias().isEmpty()) {
                            videos = sec.getCourseMedias().stream()
                                    .map(m -> new CourseDetailResponse.SectionInfo.VideoInfo(
                                            m.getMediaTitle(),
                                            m.getMediaUrl()))
                                    .collect(Collectors.toList());
                        }

                        return new CourseDetailResponse.SectionInfo(sec.getSectionTitle(), videos);
                    })
                    .collect(Collectors.toList());

            response.setSections(sections);
        }

        return response;
    }

    // 管理員搜尋課程
    public List<Course> adminSearchCourses(String keyword, String status, Long categoryId, String orderBy) {
        List<Course> courses = courseRepository.findAll();

        Stream<Course> stream = courses.stream();

        if (keyword != null && !keyword.isEmpty()) {
            stream = stream.filter(c -> c.getCourseTitle() != null && c.getCourseTitle().contains(keyword));
        }
        if (status != null && !status.isEmpty()) {
            stream = stream.filter(c -> c.getCourseStatus() != null && c.getCourseStatus().equalsIgnoreCase(status));
        }
        if (categoryId != null) {
            stream = stream.filter(c -> Objects.equals(c.getCategoryId(), categoryId));
        }

        // 預設排序
        if (orderBy != null && orderBy.equalsIgnoreCase("createdOrder-desc")) {
            stream = stream.sorted(
                    Comparator.comparing(Course::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        }

        return stream.collect(Collectors.toList());
    }

}