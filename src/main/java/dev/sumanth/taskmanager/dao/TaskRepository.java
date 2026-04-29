package dev.sumanth.taskmanager.dao;

import dev.sumanth.taskmanager.entity.TaskItem;
import dev.sumanth.taskmanager.enums.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<TaskItem, Long> {
    @Query("select task from TaskItem task join fetch task.project left join fetch task.assignee where task.project.id = :projectId order by task.createdAt desc")
    List<TaskItem> findVisibleForAdmin(@Param("projectId") Long projectId);

    @Query("select task from TaskItem task join fetch task.project left join fetch task.assignee where task.project.id = :projectId and task.assignee.id = :assigneeId order by task.createdAt desc")
    List<TaskItem> findVisibleForMember(@Param("projectId") Long projectId, @Param("assigneeId") Long assigneeId);

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    long countByProjectIdAndDueDateBeforeAndStatusNot(Long projectId, LocalDate date, TaskStatus status);
}

