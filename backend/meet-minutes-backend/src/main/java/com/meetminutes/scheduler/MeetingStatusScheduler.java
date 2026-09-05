package com.meetminutes.scheduler;

import com.meetminutes.entity.Meeting;
import com.meetminutes.entity.MeetingParticipant;
import com.meetminutes.entity.MeetingStatus;
import com.meetminutes.entity.ParticipantStatus;
import com.meetminutes.repository.MeetingRepository;
import com.meetminutes.repository.MeetingParticipantRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class MeetingStatusScheduler {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    public MeetingStatusScheduler(
            MeetingRepository meetingRepository,
            MeetingParticipantRepository meetingParticipantRepository) {

        this.meetingRepository = meetingRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void updateMeetingStatuses() {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Meeting> meetings =
                meetingRepository.findAll();

        for (Meeting meeting : meetings) {

            if (meeting.getMeetingDate() == null ||
                    meeting.getStartTime() == null ||
                    meeting.getEndTime() == null) {
                continue;
            }

            if (meeting.getStatus() == MeetingStatus.CANCELLED ||
                    meeting.getStatus() == MeetingStatus.COMPLETED) {
                continue;
            }

            LocalDateTime startDateTime =
                    LocalDateTime.of(
                            meeting.getMeetingDate(),
                            meeting.getStartTime()
                    );

            LocalDateTime endDateTime =
                    LocalDateTime.of(
                            meeting.getMeetingDate(),
                            meeting.getEndTime()
                    );

            LocalDateTime currentDateTime =
                    LocalDateTime.of(today, now);

            if (currentDateTime.isBefore(startDateTime)) {

                if (meeting.getStatus() != MeetingStatus.SCHEDULED) {
                    meeting.setStatus(MeetingStatus.SCHEDULED);
                    meetingRepository.save(meeting);
                }

            } else if (currentDateTime.isBefore(endDateTime)) {

                if (meeting.getStatus() != MeetingStatus.IN_PROGRESS) {
                    meeting.setStatus(MeetingStatus.IN_PROGRESS);
                    meetingRepository.save(meeting);
                }

            } else {

                // Meeting has ended
                if (meeting.getStatus() != MeetingStatus.COMPLETED) {
                    meeting.setStatus(MeetingStatus.COMPLETED);
                    meetingRepository.save(meeting);
                }

                // Automatically decline pending invitations
                List<MeetingParticipant> participants =
                        meetingParticipantRepository
                                .findByMeetingId(meeting.getId());

                for (MeetingParticipant participant : participants) {

                    if (participant.getStatus() == ParticipantStatus.PENDING) {
                        participant.setStatus(ParticipantStatus.DECLINED);
                        meetingParticipantRepository.save(participant);
                    }
                }
            }
        }
    }
}