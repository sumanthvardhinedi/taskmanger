package dev.sumanth.taskmanager.dto;

import dev.sumanth.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull TaskStatus status) {
}
