package com.meetminutes.service;

import com.meetminutes.dto.meeting.MeetingRequest;
import com.meetminutes.dto.meeting.MeetingResponse;
import com.meetminutes.entity.Meeting;
import com.meetminutes.entity.User;
import com.meetminutes.repository.MeetingRepository;
import com.meetminutes.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    public MeetingService(
            MeetingRepository meetingRepository,
            UserRepository userRepository) {

        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
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
                        new RuntimeException("User not found"));
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
        meeting.setStatus(request.getStatus());

        meeting.setCreatedBy(currentUser);

        Meeting savedMeeting =
                meetingRepository.save(meeting);

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

        return meetingRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public MeetingResponse getMeetingById(Long id) {

        Meeting meeting = meetingRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Meeting not found"));

        return convertToResponse(meeting);
    }

    public MeetingResponse updateMeeting(
            Long id,
            MeetingRequest request) {

        Meeting meeting = meetingRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Meeting not found"));

        verifyOwnership(meeting);

        meeting.setTitle(request.getTitle());
        meeting.setDescription(request.getDescription());
        meeting.setMeetingDate(request.getMeetingDate());
        meeting.setStartTime(request.getStartTime());
        meeting.setEndTime(request.getEndTime());
        meeting.setLocation(request.getLocation());
        meeting.setMeetingLink(request.getMeetingLink());
        meeting.setStatus(request.getStatus());

        Meeting updatedMeeting =
                meetingRepository.save(meeting);

        return convertToResponse(updatedMeeting);
    }

    public void deleteMeeting(Long id) {

        Meeting meeting = meetingRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Meeting not found"));

        verifyOwnership(meeting);

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

            throw new RuntimeException(
                    "You are not allowed to modify this meeting"
            );
        }
    }



}