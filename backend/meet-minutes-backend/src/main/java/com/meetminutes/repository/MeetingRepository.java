package com.meetminutes.repository;

import com.meetminutes.entity.Meeting;
import com.meetminutes.entity.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository
        extends JpaRepository<Meeting, Long> {

    List<Meeting> findByCreatedById(Long userId);

    List<Meeting> findByStatus(MeetingStatus status);
}