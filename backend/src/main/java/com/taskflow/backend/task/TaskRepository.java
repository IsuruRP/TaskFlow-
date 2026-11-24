package com.taskflow.backend.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
	Page<Task> findByProjectId(Long projectId, Pageable pageable);
	Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);
	@Query("select t from Task t where t.project.id = :projectId and " +
			"(lower(t.title) like lower(concat('%', :query, '%')) or lower(t.description) like lower(concat('%', :query, '%')))")
	Page<Task> search(@Param("projectId") Long projectId, @Param("query") String query, Pageable pageable);
	List<Task> findByAssigneeIdAndDueDateBeforeAndStatusNot(Long userId, LocalDate date, TaskStatus status);
	long countByAssigneeIdAndStatusNot(Long userId, TaskStatus status);
	long countByProjectId(Long projectId);
	long countByProjectIdAndStatus(Long projectId, TaskStatus status);
}
