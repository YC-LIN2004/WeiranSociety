package com.example.Backend.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.CourseSection;
import com.example.Backend.Repository.CourseMediaRepository;
import com.example.Backend.Repository.CourseSectionRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseSectionService {

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired(required = false)
    private CourseMediaRepository courseMediaRepository;

    // 取得指定課程的所有章節（依順序）
    public List<CourseSection> getSectionsByCourse(Long courseId) {
        return courseSectionRepository.findByCourse_CourseIdOrderBySectionOrderIndexAsc(courseId);
    }

    // 取得單一章節
    public CourseSection getSectionById(Long sectionId) {
        return courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("找不到章節，ID = " + sectionId));
    }

    // 新增或更新章節
    public CourseSection saveSection(CourseSection section) {
        if (section.getSectionId() == null) {
            return addSection(section);
        } else {
            return updateSection(section);
        }
    }

    // 新增章節（自動決定排序索引）
    public CourseSection addSection(CourseSection section) {
        Long courseId = section.getCourse().getCourseId();
        Integer maxIndex = courseSectionRepository.findMaxOrderIndexByCourseId(courseId);
        section.setSectionOrderIndex(maxIndex == null ? 1 : maxIndex + 1);
        section.setUpdatedAt(LocalDateTime.now());
        return courseSectionRepository.save(section);
    }

    // 更新章節（僅更新必要欄位）
    public CourseSection updateSection(CourseSection section) {
        CourseSection existing = courseSectionRepository.findById(section.getSectionId())
                .orElseThrow(() -> new RuntimeException("找不到章節，ID = " + section.getSectionId()));

        existing.setSectionTitle(section.getSectionTitle());
        existing.setSectionContent(section.getSectionContent());
        existing.setSectionOrderIndex(section.getSectionOrderIndex());
        existing.setUpdatedAt(LocalDateTime.now());

        return courseSectionRepository.save(existing);
    }

    // 刪除章節（連同底下影片一起刪除）
    @Transactional
    public void deleteSection(Long sectionId) {
        if (courseMediaRepository != null) {
            courseMediaRepository.deleteBySection_SectionId(sectionId);
        }
        courseSectionRepository.deleteById(sectionId);
    }
}
