package com.meetminutes.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "meeting_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"meeting_id", "user_id"}
                )
        }
)
public class MeetingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public MeetingParticipant() {
    }

    public Long getId() {
        return id;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}