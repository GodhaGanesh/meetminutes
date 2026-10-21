
package com.meetminutes.dto;

import java.time.LocalDateTime;

public class MyTaskResponse {

    private Long id;
    private Long meetingId;
    private String meetingTitle;
    private String task;
    private String responsiblePerson;
    private String deadline;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MyTaskResponse() {
    }

    public MyTaskResponse(
            Long id,
            Long meetingId,
            String meetingTitle,
            String task,
            String responsiblePerson,
            String deadline,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.meetingId = meetingId;
        this.meetingTitle = meetingTitle;
        this.task = task;
        this.responsiblePerson = responsiblePerson;
        this.deadline = deadline;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public String getTask() {
        return task;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
