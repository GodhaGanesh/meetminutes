package com.meetminutes.repository;

import com.meetminutes.entity.MeetingParticipant;
import com.meetminutes.entity.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface MeetingParticipantRepository
        extends JpaRepository<MeetingParticipant, Long> {

    Optional<MeetingParticipant> findByMeetingIdAndUserId(
            Long meetingId,
            Long userId
    );

    List<MeetingParticipant> findByMeetingId(
            Long meetingId
    );

    List<MeetingParticipant> findByUserIdAndStatus(
            Long userId,
            ParticipantStatus status
    );
}