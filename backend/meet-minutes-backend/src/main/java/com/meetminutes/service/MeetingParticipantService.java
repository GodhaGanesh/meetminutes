package com.meetminutes.service;

import com.meetminutes.dto.participant.ParticipantRequest;
import com.meetminutes.dto.participant.ParticipantResponse;
import com.meetminutes.entity.Meeting;
import com.meetminutes.entity.MeetingParticipant;
import com.meetminutes.entity.User;
import com.meetminutes.exception.ConflictException;
import com.meetminutes.exception.ForbiddenException;
import com.meetminutes.exception.ResourceNotFoundException;
import com.meetminutes.repository.MeetingParticipantRepository;
import com.meetminutes.repository.MeetingRepository;
import com.meetminutes.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingParticipantService {

    private final MeetingParticipantRepository participantRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    public MeetingParticipantService(
            MeetingParticipantRepository participantRepository,
            MeetingRepository meetingRepository,
            UserRepository userRepository
    ) {
        this.participantRepository = participantRepository;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
    }

    public ParticipantResponse addParticipant(
            Long meetingId,
            ParticipantRequest request,
            Long requesterId
    ) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found")
                );

        if (!meeting.getCreatedBy().getId().equals(requesterId)) {
            throw new ForbiddenException(
                    "Only the meeting creator can add participants"
            );
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
                );

        if (participantRepository
                .findByMeetingIdAndUserId(meetingId, user.getId())
                .isPresent()) {

            throw new ConflictException(
                    "User is already a participant"
            );
        }

        MeetingParticipant participant = new MeetingParticipant();

        participant.setMeeting(meeting);
        participant.setUser(user);

        MeetingParticipant saved =
                participantRepository.save(participant);

        return new ParticipantResponse(
                saved.getId(),
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }

    public List<ParticipantResponse> getParticipants(Long meetingId) {

        meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found")
                );

        return participantRepository
                .findByMeetingId(meetingId)
                .stream()
                .map(participant ->
                        new ParticipantResponse(
                                participant.getId(),
                                participant.getUser().getId(),
                                participant.getUser().getFullName(),
                                participant.getUser().getEmail()
                        )
                )
                .toList();
    }

    public void removeParticipant(
            Long meetingId,
            Long userId,
            Long requesterId
    ) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found")
                );

        if (!meeting.getCreatedBy().getId().equals(requesterId)) {
            throw new ForbiddenException(
                    "Only the meeting owner can remove participants"
            );
        }

        MeetingParticipant participant =
                participantRepository
                        .findByMeetingIdAndUserId(meetingId, userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Participant not found"
                                )
                        );

        participantRepository.delete(participant);
    }
}