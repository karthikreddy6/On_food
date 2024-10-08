package com.example.onfood;

public class User {
    private String userId;
    private String email;
    private String name; // Add name field
    private String phone; // Add phone field

    // Default constructor required for Firestore deserialization
    public User() {
    }

    public User(String userId, String email, String name, String phone) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}