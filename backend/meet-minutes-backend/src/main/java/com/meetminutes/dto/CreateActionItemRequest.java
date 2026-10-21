package com.meetminutes.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateActionItemRequest {

    @NotBlank(message = "Task description is required")
    private String task;

    private String responsiblePerson;

    private String deadline;

    public CreateActionItemRequest() {
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}