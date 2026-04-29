package dev.sumanth.taskmanager.controller;

import dev.sumanth.taskmanager.dto.AddMemberRequest;
import dev.sumanth.taskmanager.dto.CreateProjectRequest;
import dev.sumanth.taskmanager.dto.MemberResponse;
import dev.sumanth.taskmanager.dto.ProjectResponse;
import dev.sumanth.taskmanager.security.CurrentUser;
import dev.sumanth.taskmanager.entity.AppUser;
import dev.sumanth.taskmanager.entity.Project;
import dev.sumanth.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final CurrentUser currentUser;

    public ProjectController(ProjectService projectService, CurrentUser currentUser) {
        this.projectService = projectService;
        this.currentUser = currentUser;
    }

    @GetMapping
    List<ProjectResponse> list() {
        AppUser user = currentUser.require();
        return projectService.visibleProjects(user).stream()
                .map(project -> ProjectResponse.from(project, projectService.isAdmin(project.getId(), user)))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        AppUser user = currentUser.require();
        Project project = projectService.createProject(request.name(), request.description(), user);
        return ProjectResponse.from(project, true);
    }

    @GetMapping("/{projectId}/members")
    List<MemberResponse> members(@PathVariable Long projectId) {
        AppUser user = currentUser.require();
        return projectService.members(projectId, user).stream().map(MemberResponse::from).toList();
    }

    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    MemberResponse addMember(@PathVariable Long projectId, @Valid @RequestBody AddMemberRequest request) {
        AppUser user = currentUser.require();
        return MemberResponse.from(projectService.addMember(projectId, request.email(), request.role(), user));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeMember(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.removeMember(projectId, userId, currentUser.require());
    }
}

