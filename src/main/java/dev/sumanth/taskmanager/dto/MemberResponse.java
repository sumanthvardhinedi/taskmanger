package dev.sumanth.taskmanager.dto;

import dev.sumanth.taskmanager.entity.ProjectMember;
import dev.sumanth.taskmanager.enums.ProjectRole;

public record MemberResponse(Long userId, String name, String email, ProjectRole role) {
    public static MemberResponse from(ProjectMember member) {
        return new MemberResponse(member.getUser().getId(), member.getUser().getName(), member.getUser().getEmail(), member.getRole());
    }
}
