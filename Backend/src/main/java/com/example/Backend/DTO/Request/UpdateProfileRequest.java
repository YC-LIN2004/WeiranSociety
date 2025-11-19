package com.example.Backend.DTO.Request;

public class UpdateProfileRequest {

    private String bio;
    private String learningGoal;

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
}
