package com.meetminutes.controller;

import com.meetminutes.dto.recording.MeetingRecordingResponse;
import com.meetminutes.service.MeetingRecordingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/meetings/{meetingId}/recordings")
public class MeetingRecordingController {

    private final MeetingRecordingService recordingService;

    public MeetingRecordingController(
            MeetingRecordingService recordingService
    ) {
        this.recordingService = recordingService;
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MeetingRecordingResponse> uploadRecording(
            @PathVariable Long meetingId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        MeetingRecordingResponse response =
                recordingService.uploadRecording(
                        meetingId,
                        file
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<MeetingRecordingResponse>> getRecordings(
            @PathVariable Long meetingId
    ) {
        List<MeetingRecordingResponse> recordings =
                recordingService.getRecordings(meetingId);

        return ResponseEntity.ok(recordings);
    }
}