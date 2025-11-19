package com.example.Backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Backend.DTO.Response.TeacherProfileResponse;
import com.example.Backend.Entity.TeacherProfile;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.TeacherProfileRepository;
import com.example.Backend.Repository.UsersRepository;
import com.example.Backend.Service.TeacherProfileService;
import com.example.Backend.Utils.TeacherStatus;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherProfileController {

    @Autowired
    private TeacherProfileService teacherProfileService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TeacherProfileRepository teacherProfileRepository;

    // 取得全部老師
    @GetMapping
    public List<TeacherProfileResponse> getAllTeachers() {
        return teacherProfileService.getAllTeachers();
    }

    // 依 TeacherID 查詢老師
    @GetMapping("/{teacherId}")
    public Optional<TeacherProfileResponse> getTeacherById(@PathVariable Long teacherId) {
        return teacherProfileService.getTeacherById(teacherId);
    }

    // 依 UserID 查詢老師
    @GetMapping("/user/{userId}")
    public TeacherProfileResponse getTeacherByUserId(@PathVariable Long userId) {
        return teacherProfileService.getTeacherByUserId(userId);
    }

    // 依狀態查詢老師（ACTIVE / PENDING / SUSPENDED / REJECTED）
    @GetMapping("/status/{status}")
    public List<TeacherProfileResponse> getTeachersByStatus(@PathVariable String status) {
        return teacherProfileService.getTeachersByStatus(status);
    }

    // 新增或更新老師資料
    @PostMapping
    public TeacherProfileResponse saveTeacherProfile(@RequestBody TeacherProfile teacherProfile) {
        return teacherProfileService.saveTeacherProfile(teacherProfile);
    }

    // 刪除老師資料
    @DeleteMapping("/{teacherId}")
    public ResponseEntity<Map<String, Object>> deleteTeacher(@PathVariable Long teacherId) {
        teacherProfileService.deleteTeacher(teacherId);
        return ResponseEntity.ok(Map.of("message", "TeacherProfile 已刪除", "teacherId", teacherId));
    }

    // 管理員審核動作（統一回傳 DTO）

    // 審核通過老師（PENDING → ACTIVE）
    @PutMapping("/approve/{teacherId}")
    public TeacherProfileResponse approveTeacher(@PathVariable Long teacherId) {
        return teacherProfileService.updateTeacherStatus(teacherId, TeacherStatus.ACTIVE);
    }

    // 拒絕老師申請（PENDING → REJECTED）
    @PutMapping("/reject/{teacherId}")
    public TeacherProfileResponse rejectTeacher(@PathVariable Long teacherId) {
        return teacherProfileService.updateTeacherStatus(teacherId, TeacherStatus.REJECTED);
    }

    // 停權老師（ACTIVE → SUSPENDED）
    @PutMapping("/suspend/{teacherId}")
    public TeacherProfileResponse suspendTeacher(@PathVariable Long teacherId) {
        return teacherProfileService.updateTeacherStatus(teacherId, TeacherStatus.SUSPENDED);
    }

    // 復權老師（SUSPENDED → ACTIVE）
    @PutMapping("/reactivate/{teacherId}")
    public TeacherProfileResponse reactivateTeacher(@PathVariable Long teacherId) {
        return teacherProfileService.updateTeacherStatus(teacherId, TeacherStatus.ACTIVE);
    }

    // 評分
    @PostMapping("/{teacherId}/rate")
    public ResponseEntity<Map<String, Object>> rateTeacher(
            @PathVariable Long teacherId,
            @RequestBody Map<String, Object> payload) {

        System.out.println("⭐ 收到評分請求 teacherId = " + teacherId);

        Double rating = Double.valueOf(payload.get("rating").toString());
        TeacherProfile updated = teacherProfileService.rateTeacher(teacherId, rating);

        return ResponseEntity.ok(Map.of(
                "message", "感謝您的評價！",
                "averageRating", updated.getTeacherRating(),
                "ratingCount", updated.getRatingCount()));
    }

    // 老師選項列表
    @GetMapping({ "/options", "/teacher-profiles/options" })
    public ResponseEntity<List<Map<String, Object>>> getTeacherOptions() {
        List<TeacherProfile> teachers = teacherProfileRepository.findAll();

        List<Map<String, Object>> options = teachers.stream()
                .map(t -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("teacherId", t.getTeacherId());
                    try {
                        // ✅ TeacherProfile 已經關聯 Users，不用再查 Repository
                        Users user = t.getUser();
                        m.put("teacherName", (user != null && user.getUsername() != null)
                                ? user.getUsername()
                                : "未設定名稱");
                    } catch (Exception e) {
                        m.put("teacherName", "未設定名稱");
                    }
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(options);
    }
}