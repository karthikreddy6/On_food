package com.example.onfood;

public class User {
    private String userId;
    private String email;

    // Default constructor required for Firestore deserialization
    public User() {
    }

    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
