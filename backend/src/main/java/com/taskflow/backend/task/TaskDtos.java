package com.taskflow.backend.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

public final class TaskDtos {
	private TaskDtos() { }
	public record TaskRequest(@NotBlank @Size(max = 160) String title, @Size(max = 3000) String description,
			TaskPriority priority, Long assigneeId, LocalDate dueDate) { }
	public record TaskStatusRequest(TaskStatus status) { }
	public record TaskResponse(Long id, Long projectId, String title, String description, TaskStatus status,
			TaskPriority priority, Long assigneeId, String assigneeName, LocalDate dueDate, Instant createdAt) {
		public static TaskResponse from(Task task) {
			return new TaskResponse(task.getId(), task.getProject().getId(), task.getTitle(), task.getDescription(),
					task.getStatus(), task.getPriority(), task.getAssignee() == null ? null : task.getAssignee().getId(),
					task.getAssignee() == null ? null : task.getAssignee().getFullName(), task.getDueDate(), task.getCreatedAt());
		}
	}
}
