package com.meetminutes.repository;

import com.meetminutes.entity.MeetingTranscript;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingTranscriptRepository
        extends JpaRepository<MeetingTranscript, Long> {

    Optional<MeetingTranscript> findByRecordingId(Long recordingId);
}