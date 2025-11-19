package com.example.Backend.Controller;

import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Backend.Service.TeacherUploadService;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class TeacherUploadController {

    private final TeacherUploadService uploadService;

    public TeacherUploadController(TeacherUploadService uploadService) {
        this.uploadService = uploadService;
    }

    // 上傳封面圖
    @PostMapping("/uploadCover")
    public ResponseEntity<?> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            String url = uploadService.saveCover(file);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "封面上傳失敗", "error", e.getMessage()));
        }
    }

    // 上傳影片
    @PostMapping("/uploadMedia")
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {
            String url = uploadService.saveMedia(file);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "影片上傳失敗", "error", e.getMessage()));
        }
    }
}
