package com.example.Backend.DTO.Response;

import java.math.BigDecimal;

public record TeacherBriefResponse(

        // TeacherProfile.teacherId 或 Users.userId
        Long teacherId,

        // Users.realname（沒有就 username）
        String name,

        // Users.avatarUrl
        String avatarUrl,

        // TeacherProfile.bio
        String bio,

        // TeacherProfile.expertise
        String expertise,

        // TeacherProfile.teacherRating
        BigDecimal rating) {
}
