package com.meetminutes.service;

import com.meetminutes.dto.recording.MeetingRecordingResponse;
import com.meetminutes.entity.Meeting;
import com.meetminutes.entity.MeetingRecording;
import com.meetminutes.entity.ParticipantStatus;
import com.meetminutes.entity.User;
import com.meetminutes.exception.BadRequestException;
import com.meetminutes.exception.ForbiddenException;
import com.meetminutes.exception.ResourceNotFoundException;
import com.meetminutes.repository.MeetingParticipantRepository;
import com.meetminutes.repository.MeetingRecordingRepository;
import com.meetminutes.repository.MeetingRepository;
import com.meetminutes.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class MeetingRecordingService {

    private final MeetingRecordingRepository recordingRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/meeting-recordings");

    public MeetingRecordingService(
            MeetingRecordingRepository recordingRepository,
            MeetingRepository meetingRepository,
            UserRepository userRepository,
            MeetingParticipantRepository meetingParticipantRepository
    ) {
        this.recordingRepository = recordingRepository;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    public MeetingRecordingResponse uploadRecording(
            Long meetingId,
            MultipartFile file
    ) throws IOException {

        User currentUser = getCurrentUser();

        Meeting meeting = meetingRepository
                .findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found"));

        // Only the meeting owner can upload a recording
        if (!meeting.getCreatedBy().getId()
                .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    "Only the meeting owner can upload a recording"
            );
        }

        validateFile(file);

        Files.createDirectories(uploadDirectory);

        String originalFileName = file.getOriginalFilename();

        String safeFileName = UUID.randomUUID()
                + "_" + originalFileName;

        Path targetPath = uploadDirectory.resolve(safeFileName);

        Files.copy(
                file.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        MeetingRecording recording = new MeetingRecording();

        recording.setMeeting(meeting);
        recording.setFileName(originalFileName);
        recording.setFilePath(targetPath.toString());
        recording.setFileType(file.getContentType());
        recording.setFileSize(file.getSize());

        MeetingRecording savedRecording =
                recordingRepository.save(recording);

        return new MeetingRecordingResponse(
                savedRecording.getId(),
                savedRecording.getMeeting().getId(),
                savedRecording.getFileName(),
                savedRecording.getFileType(),
                savedRecording.getFileSize(),
                savedRecording.getProcessingStatus(),
                savedRecording.getUploadedAt()
        );
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please upload a file");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new BadRequestException("File name is missing");
        }

        String fileName = originalFileName.toLowerCase();

        boolean validExtension =
                fileName.endsWith(".mp3") ||
                        fileName.endsWith(".wav") ||
                        fileName.endsWith(".m4a") ||
                        fileName.endsWith(".mp4");

        if (!validExtension) {
            throw new BadRequestException(
                    "Only MP3, WAV, M4A, and MP4 files are allowed"
            );
        }

        String contentType = file.getContentType();

        boolean validContentType =
                contentType == null ||
                        contentType.equalsIgnoreCase("audio/mpeg") ||
                        contentType.equalsIgnoreCase("audio/mp3") ||
                        contentType.equalsIgnoreCase("audio/wav") ||
                        contentType.equalsIgnoreCase("audio/x-wav") ||
                        contentType.equalsIgnoreCase("audio/mp4") ||
                        contentType.equalsIgnoreCase("audio/x-m4a") ||
                        contentType.equalsIgnoreCase("video/mp4") ||
                        contentType.equalsIgnoreCase("application/octet-stream");

        if (!validContentType) {
            throw new BadRequestException(
                    "Unsupported audio/video file type: " + contentType
            );
        }
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

    public List<MeetingRecordingResponse> getRecordings(Long meetingId) {

        User currentUser = getCurrentUser();

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meeting not found"));

        if (!meeting.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(
                    "Only the meeting owner can view recordings"
            );
        }

        return recordingRepository.findByMeetingId(meetingId)
                .stream()
                .map(recording -> new MeetingRecordingResponse(
                        recording.getId(),
                        recording.getMeeting().getId(),
                        recording.getFileName(),
                        recording.getFileType(),
                        recording.getFileSize(),
                        recording.getProcessingStatus(),
                        recording.getUploadedAt()
                ))
                .toList();
    }
}