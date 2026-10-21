package com.meetminutes.repository;

import com.meetminutes.entity.MeetingMinutes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingMinutesRepository
        extends JpaRepository<MeetingMinutes, Long> {

    Optional<MeetingMinutes> findByMeetingId(Long meetingId);
}