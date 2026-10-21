package com.meetminutes.dto.recording;

import com.meetminutes.entity.RecordingStatus;

import java.time.LocalDateTime;

public class MeetingRecordingResponse {

    private Long id;
    private Long meetingId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private RecordingStatus processingStatus;
    private LocalDateTime uploadedAt;

    public MeetingRecordingResponse() {
    }

    public MeetingRecordingResponse(
            Long id,
            Long meetingId,
            String fileName,
            String fileType,
            Long fileSize,
            RecordingStatus processingStatus,
            LocalDateTime uploadedAt
    ) {
        this.id = id;
        this.meetingId = meetingId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.processingStatus = processingStatus;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public RecordingStatus getProcessingStatus() {
        return processingStatus;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}