package com.example.Backend.DTO.Response;

public class StudentProfileResponse {
    private String bio;
    private String learningGoal;
    // 購課總數動態計算
    private int totalCourses;

    public StudentProfileResponse() {
    }

    public StudentProfileResponse(String bio, String learningGoal, int totalCourses) {
        this.bio = bio;
        this.learningGoal = learningGoal;
        this.totalCourses = totalCourses;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLearningGoal() {
        return learningGoal;
    }

    public void setLearningGoal(String learningGoal) {
        this.learningGoal = learningGoal;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses = totalCourses;
    }

}
