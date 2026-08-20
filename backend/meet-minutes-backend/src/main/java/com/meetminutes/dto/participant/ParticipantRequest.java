package com.meetminutes.dto.participant;

public class ParticipantRequest {

    private Long userId;

    public ParticipantRequest() {
    }

    public ParticipantRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}