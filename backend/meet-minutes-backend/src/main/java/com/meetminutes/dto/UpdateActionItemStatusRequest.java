package com.meetminutes.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateActionItemStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    public UpdateActionItemStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}