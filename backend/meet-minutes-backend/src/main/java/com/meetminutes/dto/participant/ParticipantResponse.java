package com.meetminutes.dto.participant;

public class ParticipantResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;

    public ParticipantResponse() {
    }

    public ParticipantResponse(
            Long id,
            Long userId,
            String userName,
            String userEmail
    ) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}