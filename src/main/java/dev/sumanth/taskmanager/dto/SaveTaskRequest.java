package dev.sumanth.taskmanager.dto;

import dev.sumanth.taskmanager.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SaveTaskRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        LocalDate dueDate,
        @NotNull Priority priority,
        Long assigneeId
) {
}
