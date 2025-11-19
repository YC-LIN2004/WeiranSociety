package com.example.Backend.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.DTO.Response.CourseAdminResponse;
import com.example.Backend.Entity.Course;
import com.example.Backend.Repository.CourseAdminRepository;
import com.example.Backend.Repository.CourseRepository;

@Service
public class CourseAdminService {

    @Autowired
    private CourseAdminRepository courseAdminRepository;

    @Autowired
    private CourseRepository courseRepository;

    /** 搜尋課程（可選 keyword, status, categoryId） */
    public List<CourseAdminResponse> searchCourses(String keyword, String status, Long categoryId) {
        List<CourseAdminResponse> list = courseAdminRepository.findAllWithTeacherAndCategory();

        return list.stream()
                .filter(c -> keyword == null || c.getCourseTitle().toLowerCase().contains(keyword.toLowerCase())
                        || (c.getTeacherName() != null
                                && c.getTeacherName().toLowerCase().contains(keyword.toLowerCase())))
                .filter(c -> status == null || c.getCourseStatus().equalsIgnoreCase(status))
                .filter(c -> categoryId == null || c.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    /** 上架課程 */
    public void publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到課程 ID: " + id));
        course.setCourseStatus("Active");
        courseRepository.save(course);
    }

    /** 下架課程 */
    public void unpublishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到課程 ID: " + id));
        course.setCourseStatus("Inactive");
        courseRepository.save(course);
    }

    /** 更新課程 */
    public void updateCourse(Long id, Course updated) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到課程 ID: " + id));
        c.setCourseTitle(updated.getCourseTitle());
        c.setCategoryId(updated.getCategoryId());
        c.setTeacherId(updated.getTeacherId());
        c.setPrice(updated.getPrice());
        c.setCourseDescription(updated.getCourseDescription());
        c.setCourseStatus(updated.getCourseStatus());
        c.setCoverUrl(updated.getCoverUrl());
        courseRepository.save(c);
    }

    /** 刪除課程 */
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
