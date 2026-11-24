package com.taskflow.backend.project;

import com.taskflow.backend.common.BaseEntity;
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
@Table(name = "projects")
public class Project extends BaseEntity {
	@Column(nullable = false, length = 120)
	private String name;
	@Column(length = 2000)
	private String description;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ProjectStatus status = ProjectStatus.PLANNING;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id")
	private User owner;
	private LocalDate startDate;
	private LocalDate dueDate;

	protected Project() { }
	public Project(String name, String description, User owner, LocalDate startDate, LocalDate dueDate) {
		this.name = name; this.description = description; this.owner = owner;
		this.startDate = startDate; this.dueDate = dueDate;
	}
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public ProjectStatus getStatus() { return status; }
	public void setStatus(ProjectStatus status) { this.status = status; }
	public User getOwner() { return owner; }
	public LocalDate getStartDate() { return startDate; }
	public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
	public LocalDate getDueDate() { return dueDate; }
	public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
