package com.meetminutes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_transcripts")
public class MeetingTranscript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recording_id", nullable = false, unique = true)
    private MeetingRecording recording;

    @Column(columnDefinition = "TEXT")
    private String transcriptText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TranscriptStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = TranscriptStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MeetingTranscript() {
    }

    public Long getId() {
        return id;
    }

    public MeetingRecording getRecording() {
        return recording;
    }

    public void setRecording(MeetingRecording recording) {
        this.recording = recording;
    }

    public String getTranscriptText() {
        return transcriptText;
    }

    public void setTranscriptText(String transcriptText) {
        this.transcriptText = transcriptText;
    }

    public TranscriptStatus getStatus() {
        return status;
    }

    public void setStatus(TranscriptStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}