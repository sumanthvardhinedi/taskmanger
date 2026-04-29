package dev.sumanth.taskmanager.dto;


import jakarta.validation.constraints.Email;
import dev.sumanth.taskmanager.enums.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(
        @NotBlank @Email String email,
        @NotNull ProjectRole role
) {
}
