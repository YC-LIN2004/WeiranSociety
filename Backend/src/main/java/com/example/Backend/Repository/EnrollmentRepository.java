package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Backend.Entity.Course;
import com.example.Backend.Entity.Enrollment;
import com.example.Backend.Entity.StudentProfile;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // 查詢某學生所有修課紀錄
    List<Enrollment> findByStudent_Users_UserId(Long userId);

    // 查詢某課程的所有修課學生
    List<Enrollment> findByCourse_CourseId(Long courseId);

    // 統計某學生的修課總數
    Long countByStudent_Users_UserId(Long userId);

    boolean existsByStudentAndCourse(StudentProfile student, Course course);

    boolean existsByStudent_StudentIdAndCourse_CourseId(Long studentId, Long courseId);
}
