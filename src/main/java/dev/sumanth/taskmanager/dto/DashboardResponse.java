package dev.sumanth.taskmanager.dto;

import dev.sumanth.taskmanager.enums.TaskStatus;

import java.util.Map;

public record DashboardResponse(
        long totalTasks,
        Map<TaskStatus, Long> tasksByStatus,
        Map<String, Long> tasksPerUser,
        long overdueTasks
) {
}
