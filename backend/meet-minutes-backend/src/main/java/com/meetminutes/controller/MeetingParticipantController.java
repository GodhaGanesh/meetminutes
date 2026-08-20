package com.meetminutes.controller;

import com.meetminutes.dto.participant.ParticipantRequest;
import com.meetminutes.dto.participant.ParticipantResponse;
import com.meetminutes.security.CustomUserPrincipal;
import com.meetminutes.service.MeetingParticipantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingParticipantController {

    private final MeetingParticipantService participantService;

    public MeetingParticipantController(
            MeetingParticipantService participantService
    ) {
        this.participantService = participantService;
    }

    @PostMapping("/{meetingId}/participants")
    public ResponseEntity<ParticipantResponse> addParticipant(
            @PathVariable Long meetingId,
            @RequestBody ParticipantRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        Long requesterId = principal.getUserId();

        ParticipantResponse response =
                participantService.addParticipant(
                        meetingId,
                        request,
                        requesterId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipants(
            @PathVariable Long meetingId
    ) {

        List<ParticipantResponse> participants =
                participantService.getParticipants(meetingId);

        return ResponseEntity.ok(participants);
    }

    @DeleteMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Long meetingId,
            @PathVariable Long userId
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        Long requesterId = principal.getUserId();

        participantService.removeParticipant(
                meetingId,
                userId,
                requesterId
        );

        return ResponseEntity.noContent().build();
    }
}