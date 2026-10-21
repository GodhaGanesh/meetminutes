package com.meetminutes.service;

import com.meetminutes.dto.transcript.TranscriptResponse;
import com.meetminutes.entity.Meeting;
import com.meetminutes.entity.MeetingRecording;
import com.meetminutes.entity.MeetingTranscript;
import com.meetminutes.entity.TranscriptStatus;
import com.meetminutes.entity.User;
import com.meetminutes.exception.ForbiddenException;
import com.meetminutes.exception.ResourceNotFoundException;
import com.meetminutes.repository.MeetingRecordingRepository;
import com.meetminutes.repository.MeetingTranscriptRepository;
import com.meetminutes.repository.MeetingRepository;
import com.meetminutes.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MeetingTranscriptService {

    private final MeetingTranscriptRepository transcriptRepository;
    private final MeetingRecordingRepository recordingRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final TranscriptionService transcriptionService;

    public MeetingTranscriptService(
            MeetingTranscriptRepository transcriptRepository,
            MeetingRecordingRepository recordingRepository,
            MeetingRepository meetingRepository,
            UserRepository userRepository, TranscriptionService transcriptionService
    ) {
        this.transcriptRepository = transcriptRepository;
        this.recordingRepository = recordingRepository;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.transcriptionService = transcriptionService;
    }

    public TranscriptResponse createTranscript(Long meetingId, Long recordingId) {

        User currentUser = getCurrentUser();

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found"));

        if (!meeting.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(
                    "Only the meeting owner can start transcription"
            );
        }

        MeetingRecording recording = recordingRepository.findById(recordingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recording not found"));

        if (!recording.getMeeting().getId().equals(meetingId)) {
            throw new ResourceNotFoundException(
                    "Recording does not belong to this meeting"
            );
        }

        if (transcriptRepository.findByRecordingId(recordingId).isPresent()) {
            throw new IllegalArgumentException(
                    "Transcript already exists for this recording"
            );
        }

        MeetingTranscript transcript = new MeetingTranscript();

        transcript.setRecording(recording);
        transcript.setStatus(TranscriptStatus.PENDING);
        transcript.setTranscriptText(null);

        transcript.setStatus(TranscriptStatus.PROCESSING);
        transcriptRepository.save(transcript);

        try {
            String transcriptText = transcriptionService.transcribe(
                    recording.getFilePath()
            );

            transcript.setTranscriptText(transcriptText);
            transcript.setStatus(TranscriptStatus.COMPLETED);

        } catch (Exception e) {
            transcript.setStatus(TranscriptStatus.FAILED);
            System.err.println("Whisper transcription failed: " + e.getMessage());
        }

        MeetingTranscript savedTranscript = transcriptRepository.save(transcript);
        return toResponse(savedTranscript);

    }

    private TranscriptResponse toResponse(MeetingTranscript transcript) {

        return new TranscriptResponse(
                transcript.getId(),
                transcript.getRecording().getId(),
                transcript.getTranscriptText(),
                transcript.getStatus(),
                transcript.getCreatedAt(),
                transcript.getUpdatedAt()
        );
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Current user not found"));
    }
}