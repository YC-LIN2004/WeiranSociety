package com.example.Backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Backend.DTO.Response.CourseAdminResponse;
import com.example.Backend.Entity.Course;

@Repository
public interface CourseAdminRepository extends JpaRepository<Course, Long> {
    @Query("""
            SELECT new com.example.Backend.DTO.Response.CourseAdminResponse(
                c.courseId, c.courseTitle, cat.categoryId, cat.categoryName,
                t.teacherId, t.user.username, c.price, c.courseStatus, c.createdAt, c.coverUrl
            )
            FROM Course c
            LEFT JOIN Category cat ON c.categoryId = cat.categoryId
            LEFT JOIN TeacherProfile t ON c.teacherId = t.teacherId
            ORDER BY c.createdAt DESC
            """)
    List<CourseAdminResponse> findAllWithTeacherAndCategory();
}
