package com.meetminutes.repository;

import com.meetminutes.entity.MeetingRecording;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRecordingRepository
        extends JpaRepository<MeetingRecording, Long> {

    List<MeetingRecording> findByMeetingId(Long meetingId);

    Optional<MeetingRecording> findFirstByMeetingIdOrderByUploadedAtDesc(
            Long meetingId
    );
}