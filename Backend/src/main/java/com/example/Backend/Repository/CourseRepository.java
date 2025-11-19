package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
  // 依老師查找課程（老師後台用）
  List<Course> findByTeacherId(Long teacherId);

  // 依分類查找課程（前台課程列表用）
  List<Course> findByCategory_CategoryId(Long categoryId);

  // 模糊搜尋課程名稱
  List<Course> findByCourseTitleContainingIgnoreCase(String keyword);

  // 依老師 + 狀態 查找課程
  List<Course> findByTeacherIdAndCourseStatus(Long teacherId, String courseStatus);

  // 依狀態篩選課程（例如 ACTIVE, PENDING）
  List<Course> findByCourseStatus(String courseStatus);

  // 前台模糊搜尋 + 狀態過濾
  List<Course> findByCourseTitleContainingIgnoreCaseAndCourseStatus(String keyword, String courseStatus);

  // 依價格排序
  List<Course> findByCategory_CategoryId(Long categoryId, Sort sort);

  @Query("SELECT c FROM Course c WHERE c.teacher.teacherId = :teacherId")
  List<Course> findCoursesByTeacherProfileId(@Param("teacherId") Long teacherId);
}
