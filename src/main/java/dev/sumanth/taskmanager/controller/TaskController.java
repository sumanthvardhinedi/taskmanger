package dev.sumanth.taskmanager.controller;

import dev.sumanth.taskmanager.dto.SaveTaskRequest;
import dev.sumanth.taskmanager.dto.StatusRequest;
import dev.sumanth.taskmanager.dto.TaskResponse;
import dev.sumanth.taskmanager.security.CurrentUser;
import dev.sumanth.taskmanager.entity.AppUser;
import dev.sumanth.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;
    private final CurrentUser currentUser;

    public TaskController(TaskService taskService, CurrentUser currentUser) {
        this.taskService = taskService;
        this.currentUser = currentUser;
    }

    @GetMapping("/projects/{projectId}/tasks")
    List<TaskResponse> list(@PathVariable Long projectId) {
        AppUser user = currentUser.require();
        return taskService.visibleTasks(projectId, user).stream().map(TaskResponse::from).toList();
    }

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    TaskResponse create(@PathVariable Long projectId, @Valid @RequestBody SaveTaskRequest request) {
        return TaskResponse.from(taskService.create(projectId, currentUser.require(), request.title(), request.description(), request.dueDate(), request.priority(), request.assigneeId()));
    }

    @PutMapping("/tasks/{taskId}")
    TaskResponse update(@PathVariable Long taskId, @Valid @RequestBody SaveTaskRequest request) {
        return TaskResponse.from(taskService.update(taskId, currentUser.require(), request.title(), request.description(), request.dueDate(), request.priority(), request.assigneeId()));
    }

    @PutMapping("/tasks/{taskId}/status")
    TaskResponse updateStatus(@PathVariable Long taskId, @Valid @RequestBody StatusRequest request) {
        return TaskResponse.from(taskService.updateStatus(taskId, currentUser.require(), request.status()));
    }

    @DeleteMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long taskId) {
        taskService.delete(taskId, currentUser.require());
    }
}

