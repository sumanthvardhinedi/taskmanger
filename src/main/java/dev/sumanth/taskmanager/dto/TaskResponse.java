package dev.sumanth.taskmanager.dto;

import dev.sumanth.taskmanager.enums.Priority;
import dev.sumanth.taskmanager.entity.TaskItem;
import dev.sumanth.taskmanager.enums.TaskStatus;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        LocalDate dueDate,
        Priority priority,
        TaskStatus status,
        Long assigneeId,
        String assigneeName
) {
    public static TaskResponse from(TaskItem task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                task.getAssignee() == null ? null : task.getAssignee().getId(),
                task.getAssignee() == null ? "Unassigned" : task.getAssignee().getName()
        );
    }
}
