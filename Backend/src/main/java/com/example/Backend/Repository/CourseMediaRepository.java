package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.CourseMedia;

@Repository
public interface CourseMediaRepository extends JpaRepository<CourseMedia, Long> {

    // 查詢指定課程底下的所有影片（透過 section -> course）
    @Query("SELECT m FROM CourseMedia m WHERE m.section.course.courseId = :courseId ORDER BY m.mediaOrderIndex ASC")
    List<CourseMedia> findByCourseIdOrderByMediaOrderIndexAsc(Long courseId);

    // 查詢指定章節底下所有影片
    List<CourseMedia> findBySection_SectionIdOrderByMediaOrderIndexAsc(Long sectionId);

    // 刪除指定章節底下所有影片
    void deleteBySection_SectionId(Long sectionId);

    // 後台統計影片數（透過 section -> course）
    @Query("SELECT COUNT(m) FROM CourseMedia m WHERE m.section.course.courseId = :courseId")
    long countByCourseId(Long courseId);

    // 查詢該章節目前的最大排序值（用於新增影片時）
    @Query("SELECT MAX(m.mediaOrderIndex) FROM CourseMedia m WHERE m.section.sectionId = :sectionId")
    Integer findMaxOrderIndexBySectionId(Long sectionId);
}
