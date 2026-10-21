package com.meetminutes.dto.transcript;

import com.meetminutes.entity.TranscriptStatus;

import java.time.LocalDateTime;

public class TranscriptResponse {

    private Long id;
    private Long recordingId;
    private String transcriptText;
    private TranscriptStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TranscriptResponse(
            Long id,
            Long recordingId,
            String transcriptText,
            TranscriptStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.recordingId = recordingId;
        this.transcriptText = transcriptText;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getRecordingId() {
        return recordingId;
    }

    public String getTranscriptText() {
        return transcriptText;
    }

    public TranscriptStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}