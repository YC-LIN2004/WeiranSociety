package com.example.Backend.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.CourseMedia;
import com.example.Backend.Repository.CourseMediaRepository;

@Service
public class CourseMediaService {

    @Autowired
    private CourseMediaRepository courseMediaRepository;

    // 取得指定課程的所有媒體（依章節順序）
    public List<CourseMedia> getMediaByCourse(Long courseId) {
        return courseMediaRepository.findByCourseIdOrderByMediaOrderIndexAsc(courseId);
    }

    // 新增或更新媒體
    public CourseMedia saveMedia(CourseMedia media) {
        if (media.getCourseMediaId() == null) {
            return addMedia(media);
        } else {
            return updateMedia(media);
        }
    }

    // 新增媒體（自動設定排序）
    public CourseMedia addMedia(CourseMedia media) {
        Long sectionId = media.getSection().getSectionId();
        Integer maxIndex = courseMediaRepository.findMaxOrderIndexBySectionId(sectionId);
        media.setMediaOrderIndex(maxIndex == null ? 1 : maxIndex + 1);
        media.setUpdatedAt(LocalDateTime.now());
        return courseMediaRepository.save(media);
    }

    // 更新媒體
    public CourseMedia updateMedia(CourseMedia media) {
        CourseMedia existing = courseMediaRepository.findById(media.getCourseMediaId())
                .orElseThrow(() -> new RuntimeException("找不到媒體，ID = " + media.getCourseMediaId()));

        existing.setMediaTitle(media.getMediaTitle());
        existing.setMediaUrl(media.getMediaUrl());
        existing.setMediaOrderIndex(media.getMediaOrderIndex());
        existing.setUpdatedAt(LocalDateTime.now());

        return courseMediaRepository.save(existing);
    }

    // 刪除媒體
    public void deleteMedia(Long mediaId) {
        if (!courseMediaRepository.existsById(mediaId)) {
            throw new RuntimeException("要刪除的媒體不存在");
        }
        courseMediaRepository.deleteById(mediaId);
    }
}
