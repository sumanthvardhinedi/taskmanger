package dev.sumanth.taskmanager.dto;

import dev.sumanth.taskmanager.entity.Project;

public record ProjectResponse(Long id, String name, String description, boolean admin) {
    public static ProjectResponse from(Project project, boolean admin) {
        return new ProjectResponse(project.getId(), project.getName(), project.getDescription(), admin);
    }
}
