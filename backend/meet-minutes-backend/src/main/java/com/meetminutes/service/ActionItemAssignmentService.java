package com.meetminutes.service;

import com.meetminutes.dto.*;
import com.meetminutes.entity.*;
import com.meetminutes.exception.BadRequestException;
import com.meetminutes.exception.ResourceNotFoundException;
import com.meetminutes.repository.ActionItemRepository;
import com.meetminutes.repository.MeetingParticipantRepository;
import com.meetminutes.repository.MeetingRepository;
import com.meetminutes.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.meetminutes.exception.ForbiddenException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ActionItemAssignmentService {

    private final ActionItemRepository actionItemRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final UserRepository userRepository;

    public ActionItemAssignmentService(
            ActionItemRepository actionItemRepository,
            MeetingRepository meetingRepository,
            MeetingParticipantRepository meetingParticipantRepository,
            UserRepository userRepository
    ) {
        this.actionItemRepository = actionItemRepository;
        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ActionItem assignActionItem(
            Long meetingId,
            Long actionItemId,
            Long organizerId,
            AssignActionItemRequest request
    ) {
        // 1. Find the meeting.
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Meeting not found"));

        // 2. Verify that the requester owns the meeting.
        if (meeting.getCreatedBy() == null
                || !meeting.getCreatedBy().getId().equals(organizerId)) {
            throw new ForbiddenException(
                    "Only the meeting organizer can assign action items");
        }

        // 3. Find the action item belonging to this meeting.
        ActionItem actionItem = actionItemRepository
                .findByIdAndMeetingId(actionItemId, meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found in this meeting"));

        // 4. Find the selected registered user.
        Long userId = request.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        // 5. Verify that the selected user is a participant.
        MeetingParticipant participant =
                meetingParticipantRepository
                        .findByMeetingIdAndUserId(meetingId, userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Selected user is not a participant in this meeting"));

        // 6. Assign the task to the selected user.
        actionItem.setAssignedTo(participant.getUser());

        // 7. Save and return the updated action item.
        return actionItemRepository.save(actionItem);
    }

    @Transactional(readOnly = true)
    public List<ActionItemResponse> getMeetingActionItems(
            Long meetingId,
            Long requesterId
    ) {
        // 1. Verify that the meeting exists.
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Meeting not found"));

        // 2. Allow the meeting owner.
        boolean isOwner = meeting.getCreatedBy() != null
                && meeting.getCreatedBy().getId().equals(requesterId);

        // 3. Otherwise, verify participant membership.
        boolean isAcceptedParticipant = meetingParticipantRepository
                .findByMeetingIdAndUserId(meetingId, requesterId)
                .map(participant ->
                        participant.getStatus() == ParticipantStatus.ACCEPTED)
                .orElse(false);

        if (!isOwner && !isAcceptedParticipant) {
            throw new ForbiddenException(
                    "You must accept the meeting invitation to view action items");
        }

        // 4. Retrieve all tasks belonging to this meeting.
        List<ActionItem> actionItems =
                actionItemRepository.findByMeetingId(meetingId);

        // 5. Convert entities to response DTOs.
        return actionItems.stream()
                .map(item -> {
                    ActionItemResponse response = new ActionItemResponse();

                    response.setId(item.getId());
                    response.setMeetingId(meetingId);
                    response.setTask(item.getTask());
                    response.setResponsiblePerson(item.getResponsiblePerson());
                    response.setDeadline(item.getDeadline());
                    response.setStatus(item.getStatus());
                    response.setCreatedAt(item.getCreatedAt());
                    response.setUpdatedAt(item.getUpdatedAt());

                    if (item.getAssignedTo() != null) {
                        response.setAssignedToUserId(
                                item.getAssignedTo().getId());

                        // Use the actual User getter available in your project.
                        response.setAssignedToName(
                                item.getAssignedTo().getFullName()
                        );
                    }

                    return response;
                })
                .toList();
    }

    @Transactional
    public ActionItem createManualActionItem(
            Long meetingId,
            Long organizerId,
            CreateActionItemRequest request
    ) {
        // 1. Find the meeting.
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Meeting not found"));

        // 2. Only the meeting owner can create tasks.
        if (meeting.getCreatedBy() == null
                || !meeting.getCreatedBy().getId().equals(organizerId)) {
            throw new ForbiddenException(
                    "Only the meeting organizer can create action items");
        }

        // 3. Create the task without involving Gemini.
        ActionItem actionItem = new ActionItem();

        actionItem.setMeeting(meeting);
        actionItem.setTask(request.getTask().trim());
        actionItem.setResponsiblePerson(request.getResponsiblePerson());
        actionItem.setDeadline(request.getDeadline());

        // 4. A new task starts unassigned and pending.
        actionItem.setAssignedTo(null);
        actionItem.setStatus("PENDING");

        // 5. Save the task in PostgreSQL.
        return actionItemRepository.save(actionItem);
    }

    @Transactional
    public ActionItem unassignActionItem(
            Long meetingId,
            Long actionItemId,
            Long organizerId
    ) {
        // 1. Find the meeting.
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Meeting not found"));

        // 2. Only the meeting owner can unassign tasks.
        if (meeting.getCreatedBy() == null
                || !meeting.getCreatedBy().getId().equals(organizerId)) {
            throw new ForbiddenException(
                    "Only the meeting organizer can unassign action items");
        }

        // 3. Find the task belonging to this meeting.
        ActionItem actionItem = actionItemRepository
                .findByIdAndMeetingId(actionItemId, meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found in this meeting"));

        // 4. Remove the assignment without deleting the task.
        actionItem.setAssignedTo(null);

        return actionItemRepository.save(actionItem);
    }

    @Transactional
    public void deleteActionItem(
            Long meetingId,
            Long actionItemId,
            Long organizerId
    ) {
        // 1. Find the meeting.
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Meeting not found"));

        // 2. Verify that the requester owns the meeting.
        if (meeting.getCreatedBy() == null
                || !meeting.getCreatedBy().getId().equals(organizerId)) {
            throw new ForbiddenException(
                    "Only the meeting organizer can delete action items");
        }

        // 3. Find the task belonging to this meeting.
        ActionItem actionItem = actionItemRepository
                .findByIdAndMeetingId(actionItemId, meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action item not found in this meeting"));

        // 4. Delete the task record.
        actionItemRepository.delete(actionItem);
    }

    @Transactional
    public ActionItem updateActionItemStatus(
            Long meetingId,
            Long actionItemId,
            Long requesterId,
            UpdateActionItemStatusRequest request) {

        // 1. Check whether the meeting exists
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found"));

        ActionItem actionItem = actionItemRepository
                .findByIdAndMeetingId(actionItemId, meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Action item not found"));
        // 3. Check whether the requester owns the meeting
        boolean isOwner = meeting.getCreatedBy() != null
                && meeting.getCreatedBy().getId().equals(requesterId);

        // 4. Check whether the requester is assigned to this task
        boolean isAssignedParticipant =
                actionItem.getAssignedTo() != null
                        && actionItem.getAssignedTo().getId().equals(requesterId);

        // 5. Only the owner or assigned participant can update status
        if (!isOwner && !isAssignedParticipant) {
            throw new ForbiddenException(
                    "You are not allowed to update this task's status");
        }

        // 6. Normalize and validate the requested status
        String status = request.getStatus()
                .trim()
                .toUpperCase(Locale.ROOT);

        Set<String> allowedStatuses =
                Set.of("PENDING", "IN_PROGRESS", "COMPLETED");

        if (!allowedStatuses.contains(status)) throw new BadRequestException(
                "Invalid status. Allowed values: PENDING, IN_PROGRESS, COMPLETED"
        );

        // 7. Update and save the task
        actionItem.setStatus(status);

        return actionItemRepository.save(actionItem);
    }


    public List<MyTaskResponse> getMyTasks(Long userId) {

        return actionItemRepository.findByAssignedToId(userId)
                .stream()
                .map(item -> new MyTaskResponse(
                        item.getId(),
                        item.getMeeting().getId(),
                        item.getMeeting().getTitle(),
                        item.getTask(),
                        item.getResponsiblePerson(),
                        item.getDeadline(),
                        item.getStatus(),
                        item.getCreatedAt(),
                        item.getUpdatedAt()
                ))
                .toList();
    }

}