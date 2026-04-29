package dev.sumanth.taskmanager.dao;

import dev.sumanth.taskmanager.entity.Project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select pm.project from ProjectMember pm where pm.user.id = :userId order by pm.project.createdAt desc")
    List<Project> findVisibleProjects(@Param("userId") Long userId);
}

