package dev.sumanth.taskmanager.service;

import dev.sumanth.taskmanager.dao.ProjectMemberRepository;
import dev.sumanth.taskmanager.dao.ProjectRepository;
import dev.sumanth.taskmanager.dao.UserRepository;
import dev.sumanth.taskmanager.entity.AppUser;
import dev.sumanth.taskmanager.entity.Project;
import dev.sumanth.taskmanager.entity.ProjectMember;
import dev.sumanth.taskmanager.enums.ProjectRole;
import dev.sumanth.taskmanager.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projects;
    private final ProjectMemberRepository members;
    private final UserRepository users;

    public ProjectService(ProjectRepository projects, ProjectMemberRepository members, UserRepository users) {
        this.projects = projects;
        this.members = members;
        this.users = users;
    }

    public List<Project> visibleProjects(AppUser user) {
        return projects.findVisibleProjects(user.getId());
    }

    @Transactional
    public Project createProject(String name, String description, AppUser creator) {
        Project project = projects.save(new Project(name.trim(), description, creator));
        members.save(new ProjectMember(project, creator, ProjectRole.ADMIN));
        return project;
    }

    public Project requireProject(Long projectId) {
        return projects.findById(projectId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    public ProjectMember requireMembership(Long projectId, AppUser user) {
        return members.findByProjectIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "You are not a member of this project"));
    }

    public boolean isAdmin(Long projectId, AppUser user) {
        return members.findByProjectIdAndUserId(projectId, user.getId())
                .map(member -> member.getRole() == ProjectRole.ADMIN)
                .orElse(false);
    }

    public void requireAdmin(Long projectId, AppUser user) {
        if (!isAdmin(projectId, user)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    public List<ProjectMember> members(Long projectId, AppUser currentUser) {
        requireMembership(projectId, currentUser);
        return members.findMembers(projectId);
    }

    @Transactional
    public ProjectMember addMember(Long projectId, String email, ProjectRole role, AppUser currentUser) {
        Project project = requireProject(projectId);
        requireAdmin(projectId, currentUser);
        AppUser user = users.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        if (members.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "User is already a project member");
        }
        return members.save(new ProjectMember(project, user, role == null ? ProjectRole.MEMBER : role));
    }

    @Transactional
    public void removeMember(Long projectId, Long userId, AppUser currentUser) {
        requireAdmin(projectId, currentUser);
        if (currentUser.getId().equals(userId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Admins cannot remove themselves");
        }
        if (!members.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Project member not found");
        }
        members.deleteByProjectIdAndUserId(projectId, userId);
    }
}

