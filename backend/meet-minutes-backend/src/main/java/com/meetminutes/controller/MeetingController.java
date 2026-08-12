package com.meetminutes.controller;

import com.meetminutes.dto.meeting.MeetingRequest;
import com.meetminutes.dto.meeting.MeetingResponse;
import com.meetminutes.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    public ResponseEntity<MeetingResponse> createMeeting(
            @Valid @RequestBody MeetingRequest request) {

        MeetingResponse response =
                meetingService.createMeeting(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MeetingResponse>> getMyMeetings() {

        return ResponseEntity.ok(
                meetingService.getMyMeetings()
        );
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> getAllMeetings() {

        return ResponseEntity.ok(
                meetingService.getAllMeetings()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getMeetingById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                meetingService.getMeetingById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingResponse> updateMeeting(
            @PathVariable Long id,
            @Valid @RequestBody MeetingRequest request) {

        return ResponseEntity.ok(
                meetingService.updateMeeting(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(
            @PathVariable Long id) {

        meetingService.deleteMeeting(id);

        return ResponseEntity.noContent().build();
    }


}