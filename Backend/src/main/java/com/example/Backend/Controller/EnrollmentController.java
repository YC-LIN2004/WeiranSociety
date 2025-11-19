package com.example.Backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.DTO.Response.EnrollmentResponse;
import com.example.Backend.Service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    // 注入EnrollmentService
    @Autowired
    private EnrollmentService enrollmentService;

    // 依userId查詢修課紀錄
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getStudentEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentByUserId(userId));
    }

    // 更新課程進度
    @PatchMapping("/{enrollmentId}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable Long enrollmentId,
            @RequestParam("value") Double progress) {
        enrollmentService.updateProgress(enrollmentId, progress);
        return ResponseEntity.noContent().build();
    }

    // 退課
    @PostMapping("/{enrollmentId}/cancel")
    public ResponseEntity<?> cancelEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }

}
