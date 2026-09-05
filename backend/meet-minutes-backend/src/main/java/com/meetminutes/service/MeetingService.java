package com.meetminutes.service;

import com.meetminutes.dto.meeting.MeetingRequest;
import com.meetminutes.dto.meeting.MeetingResponse;
import com.meetminutes.entity.*;
import com.meetminutes.exception.BadRequestException;
import com.meetminutes.exception.ForbiddenException;
import com.meetminutes.exception.ResourceNotFoundException;
import com.meetminutes.repository.MeetingRepository;
import com.meetminutes.repository.UserRepository;
import com.meetminutes.repository.MeetingParticipantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    public MeetingService(
            MeetingRepository meetingRepository,
            UserRepository userRepository,
            MeetingParticipantRepository meetingParticipantRepository
    ) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    public MeetingResponse createMeeting(MeetingRequest request) {

        User currentUser = getCurrentUser();

        Meeting meeting = new Meeting();

        meeting.setTitle(request.getTitle());
        meeting.setDescription(request.getDescription());
        meeting.setMeetingDate(request.getMeetingDate());
        meeting.setStartTime(request.getStartTime());
        meeting.setEndTime(request.getEndTime());
        meeting.setLocation(request.getLocation());
        meeting.setMeetingLink(request.getMeetingLink());
        meeting.setStatus(MeetingStatus.SCHEDULED);

        if (request.getStartTime() != null &&
                request.getEndTime() != null &&
                !request.getStartTime().isBefore(request.getEndTime())) {

            throw new BadRequestException(
                    "Start time must be before end time"
            );
        }

        meeting.setCreatedBy(currentUser);

        Meeting savedMeeting =
                meetingRepository.save(meeting);
        MeetingParticipant creatorParticipant = new MeetingParticipant();

        creatorParticipant.setMeeting(savedMeeting);
        creatorParticipant.setUser(savedMeeting.getCreatedBy());
        creatorParticipant.setStatus(ParticipantStatus.ACCEPTED);

        meetingParticipantRepository.save(creatorParticipant);

        return convertToResponse(savedMeeting);
    }

    private MeetingResponse convertToResponse(Meeting meeting) {

        return new MeetingResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getDescription(),
                meeting.getMeetingDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getLocation(),
                meeting.getMeetingLink(),
                meeting.getStatus(),
                meeting.getCreatedBy().getId(),
                meeting.getCreatedBy().getFullName(),
                meeting.getCreatedAt(),
                meeting.getUpdatedAt()
        );
    }

    public List<MeetingResponse> getAllMeetings() {

        User currentUser = getCurrentUser();

        return meetingRepository
                .findAll()
                .stream()
                .filter(meeting -> {

                    if (meeting.getCreatedBy().getId().equals(currentUser.getId())) {
                        return true;
                    }

                    return meetingParticipantRepository
                            .findByMeetingIdAndUserId(
                                    meeting.getId(),
                                    currentUser.getId()
                            )
                            .map(participant ->
                                    participant.getStatus() == ParticipantStatus.ACCEPTED
                            )
                            .orElse(false);
                })
                .map(this::convertToResponse)
                .toList();
    }

    public MeetingResponse getMeetingById(Long meetingId) {

        User currentUser = getCurrentUser();

        return getMeetingById(
                meetingId,
                currentUser.getId()
        );
    }

    public MeetingResponse getMeetingById(
            Long meetingId,
            Long requesterId
    ) {

        Meeting meeting =
                getMeetingForUser(meetingId, requesterId);

        return convertToResponse(meeting);
    }

    public MeetingResponse updateMeeting(
            Long id,
            MeetingRequest request) {

        Meeting meeting = meetingRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found"));

        verifyOwnership(meeting);

        meeting.setTitle(request.getTitle());
        meeting.setDescription(request.getDescription());
        meeting.setMeetingDate(request.getMeetingDate());
        meeting.setStartTime(request.getStartTime());
        meeting.setEndTime(request.getEndTime());
        meeting.setLocation(request.getLocation());
        meeting.setMeetingLink(request.getMeetingLink());
        meeting.setStatus(MeetingStatus.SCHEDULED);

        Meeting updatedMeeting =
                meetingRepository.save(meeting);

        return convertToResponse(updatedMeeting);
    }

    public void deleteMeeting(Long id) {

        Meeting meeting = meetingRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found"));

        verifyOwnership(meeting);

        List<MeetingParticipant> participants =
                meetingParticipantRepository.findByMeetingId(id);

        meetingParticipantRepository.deleteAll(participants);

        meetingRepository.delete(meeting);
    }

    public List<MeetingResponse> getMyMeetings() {
        User currentUser = getCurrentUser();

        return meetingRepository
                .findByCreatedById(currentUser.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private void verifyOwnership(Meeting meeting) {

        User currentUser = getCurrentUser();

        if (!meeting.getCreatedBy().getId()
                .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to modify this meeting"
            );
        }
    }

    private Meeting getMeetingForUser(
            Long meetingId,
            Long userId
    ) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Meeting not found"
                        )
                );

        if (meeting.getCreatedBy().getId().equals(userId)) {
            return meeting;
        }

        MeetingParticipant participant =
                meetingParticipantRepository
                        .findByMeetingIdAndUserId(
                                meetingId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ForbiddenException(
                                        "You do not have access to this meeting"
                                )
                        );

        if (participant.getStatus() != ParticipantStatus.ACCEPTED) {
            throw new ForbiddenException(
                    "You do not have access to this meeting"
            );
        }

        return meeting;
    }
}