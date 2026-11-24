package com.taskflow.backend.task;

import com.taskflow.backend.common.BaseEntity;
import com.taskflow.backend.project.Project;
import com.taskflow.backend.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "project_id")
	private Project project;
	@Column(nullable = false, length = 160) private String title;
	@Column(length = 3000) private String description;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
	private TaskStatus status = TaskStatus.BACKLOG;
	@Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
	private TaskPriority priority = TaskPriority.MEDIUM;
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assignee_id")
	private User assignee;
	private LocalDate dueDate;
	protected Task() { }
	public Task(Project project, String title, String description) {
		this.project = project; this.title = title; this.description = description;
	}
	public Project getProject() { return project; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public TaskStatus getStatus() { return status; }
	public void setStatus(TaskStatus status) { this.status = status; }
	public TaskPriority getPriority() { return priority; }
	public void setPriority(TaskPriority priority) { this.priority = priority; }
	public User getAssignee() { return assignee; }
	public void setAssignee(User assignee) { this.assignee = assignee; }
	public LocalDate getDueDate() { return dueDate; }
	public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
