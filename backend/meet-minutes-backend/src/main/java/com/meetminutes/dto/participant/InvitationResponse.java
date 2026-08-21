package com.meetminutes.dto.participant;

import com.meetminutes.entity.ParticipantStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class InvitationResponse {

    private Long meetingId;
    private String title;
    private String description;
    private LocalDate meetingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String meetingLink;
    private Long invitedById;
    private String invitedByName;
    private ParticipantStatus status;

    public InvitationResponse(
            Long meetingId,
            String title,
            String description,
            LocalDate meetingDate,
            LocalTime startTime,
            LocalTime endTime,
            String location,
            String meetingLink,
            Long invitedById,
            String invitedByName,
            ParticipantStatus status
    ) {
        this.meetingId = meetingId;
        this.title = title;
        this.description = description;
        this.meetingDate = meetingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.meetingLink = meetingLink;
        this.invitedById = invitedById;
        this.invitedByName = invitedByName;
        this.status = status;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getMeetingDate() {
        return meetingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public Long getInvitedById() {
        return invitedById;
    }

    public String getInvitedByName() {
        return invitedByName;
    }

    public ParticipantStatus getStatus() {
        return status;
    }
}