package dev.sumanth.taskmanager.service;

import dev.sumanth.taskmanager.dao.ProjectMemberRepository;
import dev.sumanth.taskmanager.dao.TaskRepository;
import dev.sumanth.taskmanager.dao.UserRepository;
import dev.sumanth.taskmanager.entity.AppUser;
import dev.sumanth.taskmanager.enums.Priority;
import dev.sumanth.taskmanager.entity.Project;
import dev.sumanth.taskmanager.entity.TaskItem;
import dev.sumanth.taskmanager.enums.TaskStatus;
import dev.sumanth.taskmanager.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository tasks;
    private final ProjectService projectService;
    private final ProjectMemberRepository members;
    private final UserRepository users;

    public TaskService(TaskRepository tasks, ProjectService projectService, ProjectMemberRepository members, UserRepository users) {
        this.tasks = tasks;
        this.projectService = projectService;
        this.members = members;
        this.users = users;
    }

    public List<TaskItem> visibleTasks(Long projectId, AppUser user) {
        projectService.requireMembership(projectId, user);
        if (projectService.isAdmin(projectId, user)) {
            return tasks.findVisibleForAdmin(projectId);
        }
        return tasks.findVisibleForMember(projectId, user.getId());
    }

    @Transactional
    public TaskItem create(Long projectId, AppUser currentUser, String title, String description, LocalDate dueDate, Priority priority, Long assigneeId) {
        projectService.requireAdmin(projectId, currentUser);
        Project project = projectService.requireProject(projectId);
        AppUser assignee = assignee(projectId, assigneeId);
        return tasks.save(new TaskItem(project, currentUser, assignee, title.trim(), description, dueDate, priority));
    }

    @Transactional
    public TaskItem update(Long taskId, AppUser currentUser, String title, String description, LocalDate dueDate, Priority priority, Long assigneeId) {
        TaskItem task = requireTask(taskId);
        projectService.requireAdmin(task.getProject().getId(), currentUser);
        AppUser assignee = assignee(task.getProject().getId(), assigneeId);
        task.update(title.trim(), description, dueDate, priority, assignee);
        return task;
    }

    @Transactional
    public TaskItem updateStatus(Long taskId, AppUser currentUser, TaskStatus status) {
        TaskItem task = requireTask(taskId);
        boolean admin = projectService.isAdmin(task.getProject().getId(), currentUser);
        boolean assigned = task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId());
        if (!admin && !assigned) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can update only assigned tasks");
        }
        task.setStatus(status);
        return task;
    }

    @Transactional
    public void delete(Long taskId, AppUser currentUser) {
        TaskItem task = requireTask(taskId);
        projectService.requireAdmin(task.getProject().getId(), currentUser);
        tasks.delete(task);
    }

    public TaskItem requireTask(Long taskId) {
        return tasks.findById(taskId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private AppUser assignee(Long projectId, Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        AppUser assignee = users.findById(assigneeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Assignee not found"));
        if (!members.existsByProjectIdAndUserId(projectId, assigneeId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Assignee must be a project member");
        }
        return assignee;
    }
}

