package com.meetminutes.repository;

import com.meetminutes.entity.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findByMeetingId(Long meetingId);

    List<ActionItem> findByAssignedToId(Long userId);

    Optional<ActionItem> findByIdAndMeetingId(
            Long id,
            Long meetingId
    );
}