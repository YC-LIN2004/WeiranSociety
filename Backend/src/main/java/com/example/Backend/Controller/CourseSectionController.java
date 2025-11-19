package com.example.Backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Entity.CourseSection;
import com.example.Backend.Repository.CourseSectionRepository;
import com.example.Backend.Service.CourseSectionService;

@RestController
@RequestMapping("/api/course-section")
@CrossOrigin(origins = "*")
public class CourseSectionController {

    @Autowired
    private CourseSectionService courseSectionService;

    // 新增或更新章節
    @PostMapping
    public ResponseEntity<CourseSection> saveSection(@RequestBody CourseSection section) {
        CourseSection saved = courseSectionService.saveSection(section);
        return ResponseEntity.ok(saved);
    }

    // 刪除章節
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Map<String, String>> deleteSection(@PathVariable Long sectionId) {
        courseSectionService.deleteSection(sectionId);
        return ResponseEntity.ok(Map.of("message", "章節刪除成功"));
    }

    private final CourseSectionRepository courseSectionRepository;

    public CourseSectionController(CourseSectionRepository courseSectionRepository) {
        this.courseSectionRepository = courseSectionRepository;
    }

    // 根據 courseId 取得所有章節（for Vue 右側章節列表）
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getSectionsWithMedia(@PathVariable Long courseId) {
        List<CourseSection> sections = courseSectionRepository.findSectionsByCourseId(courseId);

        List<Map<String, Object>> result = sections.stream().map(s -> {
            Map<String, Object> sectionMap = new HashMap<>();
            sectionMap.put("sectionId", s.getSectionId());
            sectionMap.put("sectionTitle",
                    s.getSectionTitle() != null ? s.getSectionTitle() : "章節 " + s.getSectionOrderIndex());
            sectionMap.put("sectionOrderIndex", s.getSectionOrderIndex());

            // 🔹 每個章節的影片列表
            List<Map<String, Object>> videos = s.getCourseMedias().stream().map(m -> {
                Map<String, Object> videoMap = new HashMap<>();
                videoMap.put("mediaId", m.getCourseMediaId());
                videoMap.put("title", m.getMediaTitle());
                videoMap.put("url", m.getMediaUrl());
                videoMap.put("order", m.getMediaOrderIndex());
                return videoMap;
            }).collect(Collectors.toList());

            sectionMap.put("videos", videos);
            return sectionMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}