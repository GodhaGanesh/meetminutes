
package com.meetminutes.service;

import com.meetminutes.entity.ActionItem;
import com.meetminutes.entity.Meeting;
import com.meetminutes.entity.MeetingMinutes;
import com.meetminutes.repository.ActionItemRepository;
import com.meetminutes.repository.MeetingMinutesRepository;
import com.meetminutes.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

@Service
public class MeetingMinutesGenerationService {

    private final GeminiService geminiService;
    private final MeetingRepository meetingRepository;
    private final MeetingMinutesRepository meetingMinutesRepository;
    private final ActionItemRepository actionItemRepository;

    public MeetingMinutesGenerationService(
            GeminiService geminiService,
            MeetingRepository meetingRepository,
            MeetingMinutesRepository meetingMinutesRepository,
            ActionItemRepository actionItemRepository
    ) {
        this.geminiService = geminiService;
        this.meetingRepository = meetingRepository;
        this.meetingMinutesRepository = meetingMinutesRepository;
        this.actionItemRepository = actionItemRepository;
    }

    @Transactional
    public MeetingMinutes generateAndSave(Long meetingId, String transcriptText) {

        if (transcriptText == null || transcriptText.isBlank()) {
            throw new IllegalArgumentException("Transcript text cannot be empty");
        }

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Meeting not found: " + meetingId));

        JsonNode result = geminiService.generateMeetingMinutesJson(transcriptText);

        MeetingMinutes minutes = meetingMinutesRepository
                .findByMeetingId(meetingId)
                .orElseGet(MeetingMinutes::new);

        minutes.setMeeting(meeting);
        minutes.setSummary(result.path("summary").asText(""));
        minutes.setDiscussionNotes(
                result.path("discussionPoints").toString()
        );
        minutes.setDecisions(
                result.path("decisions").toString()
        );

        MeetingMinutes savedMinutes =
                meetingMinutesRepository.save(minutes);

        // Replace previous generated action items for this meeting.
        actionItemRepository.deleteAll(
                actionItemRepository.findByMeetingId(meetingId)
        );

        JsonNode actionItems = result.path("actionItems");

        for (JsonNode item : actionItems) {
            if (!item.path("task").isTextual()
                    || item.path("task").asText().isBlank()) {
                continue;
            }

            ActionItem actionItem = new ActionItem();
            actionItem.setMeeting(meeting);
            actionItem.setTask(item.path("task").asText());
            actionItem.setResponsiblePerson(
                    item.path("responsiblePerson").asText("Not specified")
            );
            actionItem.setDeadline(
                    item.path("deadline").asText("Not specified")
            );
            actionItem.setStatus("PENDING");

            actionItemRepository.save(actionItem);
        }

        return savedMinutes;
    }
}
