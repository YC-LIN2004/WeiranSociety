package com.example.Backend.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Backend.DTO.Response.CourseAdminResponse;
import com.example.Backend.Entity.Course;
import com.example.Backend.Service.CourseAdminService;

@RestController
@RequestMapping("/api/courses/admin")
@CrossOrigin(origins = "*")
public class CourseAdminController {

    @Autowired
    private CourseAdminService courseAdminService;

    // 🔍 搜尋課程
    @GetMapping("/search")
    public List<CourseAdminResponse> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId) {
        return courseAdminService.searchCourses(keyword, status, categoryId);
    }

    // 🟢 上架
    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long id) {
        courseAdminService.publishCourse(id);
        return ResponseEntity.ok(Map.of("message", "課程已上架"));
    }

    // ⚫ 下架
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishCourse(@PathVariable Long id) {
        courseAdminService.unpublishCourse(id);
        return ResponseEntity.ok(Map.of("message", "課程已下架"));
    }

    // ✏️ 編輯課程
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course updated) {
        courseAdminService.updateCourse(id, updated);
        return ResponseEntity.ok(Map.of("message", "課程更新成功"));
    }

    // ❌ 刪除課程
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseAdminService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "課程已刪除"));
    }
}
