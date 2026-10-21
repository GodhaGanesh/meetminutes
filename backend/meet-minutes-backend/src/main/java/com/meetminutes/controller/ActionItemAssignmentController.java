package com.meetminutes.controller;

import com.meetminutes.dto.*;
import com.meetminutes.entity.ActionItem;
import com.meetminutes.security.CustomUserPrincipal;
import com.meetminutes.service.ActionItemAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings/{meetingId}/action-items")
public class ActionItemAssignmentController {

    private final ActionItemAssignmentService assignmentService;

    public ActionItemAssignmentController(
            ActionItemAssignmentService assignmentService
    ) {
        this.assignmentService = assignmentService;
    }

    @PutMapping("/{actionItemId}/assign")
    public ResponseEntity<?> assignActionItem(
            @PathVariable Long meetingId,
            @PathVariable Long actionItemId,
            @RequestBody AssignActionItemRequest request,
            Authentication authentication
    ) {
        // Get the authenticated user's ID from the security principal.
        Object principal = authentication.getPrincipal();

        Long organizerId;

        if (principal instanceof com.meetminutes.security.CustomUserPrincipal userPrincipal) {
            organizerId = userPrincipal.getUserId();
        } else {
            throw new IllegalStateException(
                    "Authenticated user ID is unavailable");
        }

        System.out.println("Authenticated organizer ID: " + organizerId);

        ActionItem actionItem = assignmentService.assignActionItem(
                meetingId,
                actionItemId,
                organizerId,
                request
        );

        return ResponseEntity.ok(Map.of(
                "message", "Action item assigned successfully",
                "actionItemId", actionItem.getId(),
                "meetingId", actionItem.getMeeting().getId(),
                "assignedToUserId", actionItem.getAssignedTo().getId(),
                "task", actionItem.getTask(),
                "status", actionItem.getStatus()
        ));
    }

    @GetMapping
    public ResponseEntity<?> getMeetingActionItems(
            @PathVariable Long meetingId,
            Authentication authentication
    ) {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof
                com.meetminutes.security.CustomUserPrincipal userPrincipal)) {
            throw new IllegalStateException(
                    "Authenticated user ID is unavailable");
        }

        Long requesterId = userPrincipal.getUserId();

        return ResponseEntity.ok(
                assignmentService.getMeetingActionItems(
                        meetingId,
                        requesterId
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> createManualActionItem(
            @PathVariable Long meetingId,
            @jakarta.validation.Valid
            @RequestBody CreateActionItemRequest request,
            Authentication authentication
    ) {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof
                com.meetminutes.security.CustomUserPrincipal userPrincipal)) {
            throw new IllegalStateException(
                    "Authenticated user ID is unavailable");
        }

        Long organizerId = userPrincipal.getUserId();

        ActionItem actionItem =
                assignmentService.createManualActionItem(
                        meetingId,
                        organizerId,
                        request
                );

        ActionItemResponse response = new ActionItemResponse();

        response.setId(actionItem.getId());
        response.setMeetingId(meetingId);
        response.setTask(actionItem.getTask());
        response.setResponsiblePerson(actionItem.getResponsiblePerson());
        response.setDeadline(actionItem.getDeadline());
        response.setStatus(actionItem.getStatus());
        response.setAssignedToUserId(null);
        response.setAssignedToName(null);
        response.setCreatedAt(actionItem.getCreatedAt());
        response.setUpdatedAt(actionItem.getUpdatedAt());

        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{actionItemId}/assignment")
    public ResponseEntity<?> unassignActionItem(
            @PathVariable Long meetingId,
            @PathVariable Long actionItemId,
            Authentication authentication
    ) {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof
                com.meetminutes.security.CustomUserPrincipal userPrincipal)) {
            throw new IllegalStateException(
                    "Authenticated user ID is unavailable");
        }

        Long organizerId = userPrincipal.getUserId();

        ActionItem actionItem = assignmentService.unassignActionItem(
                meetingId,
                actionItemId,
                organizerId
        );

        return ResponseEntity.ok(Map.of(
                "message", "Task unassigned successfully",
                "actionItemId", actionItem.getId(),
                "status", actionItem.getStatus()
        ));
    }

    @DeleteMapping("/{actionItemId}")
    public ResponseEntity<?> deleteActionItem(
            @PathVariable Long meetingId,
            @PathVariable Long actionItemId,
            Authentication authentication
    ) {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof
                com.meetminutes.security.CustomUserPrincipal userPrincipal)) {
            throw new IllegalStateException(
                    "Authenticated user ID is unavailable");
        }

        Long organizerId = userPrincipal.getUserId();

        assignmentService.deleteActionItem(
                meetingId,
                actionItemId,
                organizerId
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{actionItemId}/status")
    public ResponseEntity<?> updateActionItemStatus(
            @PathVariable Long meetingId,
            @PathVariable Long actionItemId,
            @Valid @RequestBody UpdateActionItemStatusRequest request,
            Authentication authentication) {

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof com.meetminutes.security.CustomUserPrincipal userPrincipal)) {
            throw new IllegalStateException(
                    "Authenticated user ID is unavailable");
        }

        Long requesterId = userPrincipal.getUserId();

        ActionItem actionItem = assignmentService.updateActionItemStatus(
                meetingId,
                actionItemId,
                requesterId,
                request);

        return ResponseEntity.ok(Map.of(
                "message", "Task status updated successfully",
                "actionItemId", actionItem.getId(),
                "status", actionItem.getStatus()
        ));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<MyTaskResponse>> getMyTasks(
            Authentication authentication) {

        CustomUserPrincipal userPrincipal =
                (CustomUserPrincipal) authentication.getPrincipal();

        Long userId = userPrincipal.getUserId();

        return ResponseEntity.ok(
                assignmentService.getMyTasks(userId));
    }
}