package com.meetminutes.controller;

import com.meetminutes.dto.MyTaskResponse;
import com.meetminutes.entity.ActionItem;
import com.meetminutes.security.CustomUserPrincipal;
import com.meetminutes.service.ActionItemAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/action-items")
public class MyTasksController {

    private final ActionItemAssignmentService assignmentService;

    public MyTasksController(
            ActionItemAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<MyTaskResponse>> getMyTasks(
            Authentication authentication) {

        CustomUserPrincipal userPrincipal =
                (CustomUserPrincipal) authentication.getPrincipal();

        Long userId = userPrincipal.getUserId();

        List<MyTaskResponse> tasks =
                assignmentService.getMyTasks(userId);

        return ResponseEntity.ok(tasks);
    }
}