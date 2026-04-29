package dev.sumanth.taskmanager.entity;

import dev.sumanth.taskmanager.enums.Priority;
import dev.sumanth.taskmanager.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class TaskItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser creator;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser assignee;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected TaskItem() {
    }

    public TaskItem(Project project, AppUser creator, AppUser assignee, String title, String description, LocalDate dueDate, Priority priority) {
        this.project = project;
        this.creator = creator;
        this.assignee = assignee;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority == null ? Priority.MEDIUM : priority;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public AppUser getAssignee() {
        return assignee;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void update(String title, String description, LocalDate dueDate, Priority priority, AppUser assignee) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.assignee = assignee;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}

