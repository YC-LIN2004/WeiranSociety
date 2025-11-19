package com.example.Backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.DTO.Request.CourseRequest;
import com.example.Backend.DTO.Request.CourseUpdateRequest;
import com.example.Backend.DTO.Response.CourseDetailResponse;
import com.example.Backend.DTO.Response.CourseListResponse;
import com.example.Backend.Entity.Course;
import com.example.Backend.Repository.CourseRepository;
import com.example.Backend.Service.CourseService;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    // 取得全部課程（含篩選或排序）
    @GetMapping
    public List<CourseListResponse> getAllCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "desc") String priceOrder) {

        List<Course> result = courseRepository.findAll();
        System.out.println("keyword = " + keyword + ", categoryId = " + categoryId + ", priceOrder = " + priceOrder);
        // 🔍 關鍵字搜尋（比對課程名稱 + 描述 + 分類名稱，支援中文）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.trim().toLowerCase();
            result = result.stream()
                    .filter(c -> {
                        boolean matchTitle = c.getCourseTitle() != null &&
                                c.getCourseTitle().toLowerCase().contains(lowerKeyword);
                        boolean matchDesc = c.getCourseDescription() != null &&
                                c.getCourseDescription().toLowerCase().contains(lowerKeyword);
                        boolean matchCategory = c.getCategory() != null &&
                                c.getCategory().getCategoryName() != null &&
                                c.getCategory().getCategoryName().toLowerCase().contains(lowerKeyword);
                        return matchTitle || matchDesc || matchCategory;
                    })
                    .toList();
        }

        // 🔽 分類篩選
        if (categoryId != null) {
            result = result.stream()
                    .filter(c -> c.getCategoryId().equals(categoryId))
                    .toList();
        }

        // 💰 價格排序
        result = result.stream()
                .sorted((a, b) -> {
                    int cmp = a.getPrice().compareTo(b.getPrice());
                    return "asc".equalsIgnoreCase(priceOrder) ? cmp : -cmp;
                })
                .toList();

        // ✅ 回傳簡化版課程列表
        return result.stream()
                .map(CourseListResponse::fromEntity)
                .toList();

    }

    // 依課程 ID 查詢單筆
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseById(@PathVariable Long courseId) {
        try {
            return ResponseEntity.ok(courseService.getCourseDetail(courseId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 依老師 ID 查課程
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Map<String, Object>>> getCoursesByTeacherId(@PathVariable Long teacherId) {
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);

        List<Map<String, Object>> result = courses.stream()
                .map(course -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseId", course.getCourseId());
                    map.put("courseTitle", course.getCourseTitle());
                    map.put("price", course.getPrice());
                    map.put("coverUrl", course.getCoverUrl());
                    map.put("categoryName",
                            course.getCategory() != null ? course.getCategory().getCategoryName() : "未分類");
                    map.put("updatedAt", course.getUpdatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 模糊搜尋課程名稱或描述
    @GetMapping("/search")
    public List<Course> searchCourses(@RequestParam("keyword") String keyword) {
        return courseService.searchCoursesByKeyword(keyword);
    }

    // 新增課程
    @PostMapping("/createFull")
    public Course createFullCourse(@RequestBody CourseRequest req) {
        return courseService.createCourseWithSections(req);
    }

    // 更新課程
    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable Long courseId, @RequestBody Course updatedCourse) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("找不到課程 ID: " + courseId));

        course.setCourseTitle(updatedCourse.getCourseTitle());
        course.setCategoryId(updatedCourse.getCategoryId());
        course.setTeacherId(updatedCourse.getTeacherId());
        course.setPrice(updatedCourse.getPrice());
        course.setCourseStatus(updatedCourse.getCourseStatus());
        course.setCourseDescription(updatedCourse.getCourseDescription());
        course.setCoverUrl(updatedCourse.getCoverUrl());
        courseRepository.save(course);

        return ResponseEntity.ok(Map.of("message", "課程更新成功"));
    }

    // 刪除課程
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(Map.of("message", "課程刪除成功"));
    }

    // === 取得單一課程詳細資訊（含章節） ===
    @GetMapping("/detail/{courseId}")
    public ResponseEntity<?> getCourseDetail(@PathVariable Long courseId) {
        try {
            CourseDetailResponse response = courseService.getCourseDetail(courseId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{courseId}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("找不到課程 ID: " + courseId));
        course.setCourseStatus("Active");
        courseRepository.save(course);
        return ResponseEntity.ok(Map.of("message", "課程已上架"));
    }

    @PutMapping("/{courseId}/unpublish")
    public ResponseEntity<?> unpublishCourse(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("找不到課程 ID: " + courseId));
        course.setCourseStatus("Inactive");
        courseRepository.save(course);
        return ResponseEntity.ok(Map.of("message", "課程已下架"));
    }

}
