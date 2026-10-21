
package com.meetminutes.controller;

import com.meetminutes.entity.MeetingMinutes;
import com.meetminutes.service.MeetingMinutesGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
public class MeetingMinutesGenerationController {

    private final MeetingMinutesGenerationService generationService;

    public MeetingMinutesGenerationController(
            MeetingMinutesGenerationService generationService
    ) {
        this.generationService = generationService;
    }

    @PostMapping("/{meetingId}/generate-minutes")
    public ResponseEntity<?> generateMinutes(
            @PathVariable Long meetingId,
            @RequestBody Map<String, String> request
    ) {
        String transcriptText = request.get("transcriptText");

        MeetingMinutes minutes =
                generationService.generateAndSave(meetingId, transcriptText);

        return ResponseEntity.ok(Map.of(
                "message", "Meeting minutes and action items generated successfully",
                "meetingId", meetingId,
                "minutesId", minutes.getId(),
                "summary", minutes.getSummary(),
                "discussionPoints", minutes.getDiscussionNotes(),
                "decisions", minutes.getDecisions()
        ));
    }
}
