package com.example.Backend.DTO.Response;

public record TeacherOptionResponse(

        // 對應 TeacherProfile.teacherId
        Long teacherId,

        // Users.realname（優先），否則 username
        String name) {
}