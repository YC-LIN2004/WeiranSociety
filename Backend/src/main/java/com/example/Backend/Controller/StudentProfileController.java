package com.example.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Backend.DTO.Response.StudentProfileResponse;
import com.example.Backend.Entity.StudentProfile;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.StudentProfileRepository;
import com.example.Backend.Repository.EnrollmentRepository;
import com.example.Backend.Utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class StudentProfileController {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private JwtUtils jwtUtils;

    // ✅ 取得個人資料（若無則建立）
    @GetMapping
    public ResponseEntity<StudentProfileResponse> getProfile(HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        // 查找 profile
        StudentProfile profile = studentProfileRepository.findByUsers_UserId(userId)
                .orElseGet(() -> {
                    StudentProfile newProfile = new StudentProfile();
                    newProfile.setUsers(new Users(userId));
                    newProfile.setBio("");
                    newProfile.setLearningGoal("");
                    return studentProfileRepository.save(newProfile);
                });

        // 查詢已購課程總數
        long totalCourses = enrollmentRepository.countByStudent_Users_UserId(userId);

        // 回傳 DTO
        StudentProfileResponse res = new StudentProfileResponse();
        res.setBio(profile.getBio());
        res.setLearningGoal(profile.getLearningGoal());
        res.setTotalCourses((int) totalCourses);

        return ResponseEntity.ok(res);
    }

    // ✅ 更新個人資料
    @PutMapping
    public ResponseEntity<String> updateProfile(HttpServletRequest request,
            @RequestBody StudentProfile profileUpdate) {

        Long userId = jwtUtils.getUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("無效的 Token");
        }

        StudentProfile profile = studentProfileRepository.findByUsers_UserId(userId)
                .orElseGet(() -> {
                    StudentProfile newProfile = new StudentProfile();
                    newProfile.setUsers(new Users(userId));
                    return newProfile;
                });

        if (profileUpdate.getBio() != null)
            profile.setBio(profileUpdate.getBio());
        if (profileUpdate.getLearningGoal() != null)
            profile.setLearningGoal(profileUpdate.getLearningGoal());

        studentProfileRepository.save(profile);

        return ResponseEntity.ok("資料更新成功 ✅");
    }
}
