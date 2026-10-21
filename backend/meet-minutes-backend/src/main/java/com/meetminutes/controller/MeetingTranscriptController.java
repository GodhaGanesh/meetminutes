package com.meetminutes.controller;

import com.meetminutes.dto.transcript.TranscriptResponse;
import com.meetminutes.service.MeetingTranscriptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetings/{meetingId}/recordings/{recordingId}/transcript")
public class MeetingTranscriptController {

    private final MeetingTranscriptService transcriptService;

    public MeetingTranscriptController(
            MeetingTranscriptService transcriptService
    ) {
        this.transcriptService = transcriptService;
    }

    @PostMapping
    public ResponseEntity<TranscriptResponse> createTranscript(
            @PathVariable Long meetingId,
            @PathVariable Long recordingId
    ) {

        TranscriptResponse response =
                transcriptService.createTranscript(
                        meetingId,
                        recordingId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}