package com.example.Backend.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.DTO.Response.StudentProfileResponse;
import com.example.Backend.Entity.StudentProfile;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.EnrollmentRepository;
import com.example.Backend.Repository.StudentProfileRepository;
import com.example.Backend.Repository.UsersRepository;
import com.example.Backend.Utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StudentProfileService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtUtils jwtUtils;

    // 撈取個人資料/若無自動建立
    public StudentProfileResponse getProfile(HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        if (userId == null) {
            throw new RuntimeException("Token 無效或未登入");
        }

        Optional<StudentProfile> optionalProfile = studentProfileRepository.findByUsers_UserId(userId);
        StudentProfile profile = optionalProfile.orElseGet(() -> createProfile(userId));

        long totalCourses = 0L;
        try {
            totalCourses = enrollmentRepository.countByStudent_Users_UserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ 無法取得購課數：" + e.getMessage());
        }

        return new StudentProfileResponse(
                profile.getBio() != null ? profile.getBio() : "",
                profile.getLearningGoal() != null ? profile.getLearningGoal() : "",
                (int) totalCourses);
    }

    // 自動建立學生主表
    private StudentProfile createProfile(Long userId) {
        Users users = usersRepository.findByUserId(userId);
        if (users == null) {
            users = new Users();
            users.setUserID(userId);
        }

        StudentProfile newProfile = new StudentProfile();
        newProfile.setUsers(users);
        newProfile.setBio("");
        newProfile.setLearningGoal("");

        return studentProfileRepository.save(newProfile);
    }
}