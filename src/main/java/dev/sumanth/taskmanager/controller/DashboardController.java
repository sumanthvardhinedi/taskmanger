package dev.sumanth.taskmanager.controller;

import dev.sumanth.taskmanager.security.CurrentUser;
import dev.sumanth.taskmanager.entity.TaskItem;
import dev.sumanth.taskmanager.enums.TaskStatus;
import dev.sumanth.taskmanager.entity.AppUser;
import dev.sumanth.taskmanager.dto.DashboardResponse;
import dev.sumanth.taskmanager.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}/dashboard")
public class DashboardController {
    private final TaskService taskService;
    private final CurrentUser currentUser;

    public DashboardController(TaskService taskService, CurrentUser currentUser) {
        this.taskService = taskService;
        this.currentUser = currentUser;
    }

    @GetMapping
    DashboardResponse dashboard(@PathVariable Long projectId) {
        AppUser user = currentUser.require();
        List<TaskItem> tasks = taskService.visibleTasks(projectId, user);
        Map<TaskStatus, Long> byStatus = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            byStatus.put(status, 0L);
        }
        Map<String, Long> perUser = new LinkedHashMap<>();
        long overdue = 0;
        LocalDate today = LocalDate.now();

        for (TaskItem task : tasks) {
            byStatus.put(task.getStatus(), byStatus.get(task.getStatus()) + 1);
            String assignee = task.getAssignee() == null ? "Unassigned" : task.getAssignee().getName();
            perUser.put(assignee, perUser.getOrDefault(assignee, 0L) + 1);
            if (task.getDueDate() != null && task.getDueDate().isBefore(today) && task.getStatus() != TaskStatus.DONE) {
                overdue++;
            }
        }

        return new DashboardResponse(tasks.size(), byStatus, perUser, overdue);
    }
}

