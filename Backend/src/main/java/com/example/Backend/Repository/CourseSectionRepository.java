package com.example.Backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Course;
import com.example.Backend.Entity.CourseSection;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

    // 查詢某課程底下所有章節（依照順序排列）
    List<CourseSection> findByCourse_CourseIdOrderBySectionOrderIndexAsc(Long courseId);

    // 查詢單一章節（含課程資訊）
    Optional<CourseSection> findBySectionIdAndCourse_CourseId(Long sectionId, Long courseId);

    // 刪除指定課程的所有章節
    void deleteByCourse_CourseId(Long courseId);

    // 取得章節總數（統計用）
    long countByCourse_CourseId(Long courseId);

    @Query("SELECT MAX(s.sectionOrderIndex) FROM CourseSection s WHERE s.course.courseId = :courseId")
    Integer findMaxOrderIndexByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT s FROM CourseSection s WHERE s.course.courseId = :courseId ORDER BY s.sectionOrderIndex")
    List<CourseSection> findSectionsByCourseId(@Param("courseId") Long courseId);

    // 依據課程找章節（用於更新或刪除時）
    List<CourseSection> findByCourse(Course course);
}
