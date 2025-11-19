package com.example.Backend.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.Backend.Entity.CourseMedia;
import com.example.Backend.Service.CourseMediaService;

@RestController
@RequestMapping("/api/course-media")
@CrossOrigin(origins = "*")
public class CourseMediaController {

    @Autowired
    private CourseMediaService courseMediaService;

    // 取得課程媒體
    @GetMapping("/{courseId}")
    public List<CourseMedia> getMediaByCourse(@PathVariable Long courseId) {
        return courseMediaService.getMediaByCourse(courseId);
    }

    // 新增或更新媒體
    @PostMapping
    public CourseMedia saveMedia(@RequestBody CourseMedia media) {
        return courseMediaService.saveMedia(media);
    }

    // 刪除媒體
    @DeleteMapping("/{mediaId}")
    public void deleteMedia(@PathVariable Long mediaId) {
        courseMediaService.deleteMedia(mediaId);
    }
}
