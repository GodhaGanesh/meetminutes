package com.meetminutes.dto;

public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String profileImageUrl;
    private String role;
    private boolean isActive;

    public UserResponse() {
    }

    public UserResponse(
            Long id,
            String fullName,
            String email,
            String profileImageUrl,
            String role,
            boolean isActive) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}