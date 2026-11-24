package com.taskflow.backend.task;

import com.taskflow.backend.common.ForbiddenOperationException;
import com.taskflow.backend.common.ResourceNotFoundException;
import com.taskflow.backend.notification.NotificationService;
import com.taskflow.backend.project.Project;
import com.taskflow.backend.project.ProjectAccessService;
import com.taskflow.backend.project.ProjectMemberRepository;
import com.taskflow.backend.task.TaskDtos.TaskRequest;
import com.taskflow.backend.task.TaskDtos.TaskResponse;
import com.taskflow.backend.user.Role;
import com.taskflow.backend.user.User;
import com.taskflow.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
	private final TaskRepository tasks; private final UserRepository users; private final ProjectAccessService access;
	private final ProjectMemberRepository members; private final NotificationService notifications;
	public TaskService(TaskRepository tasks, UserRepository users, ProjectAccessService access,
			ProjectMemberRepository members, NotificationService notifications) {
		this.tasks = tasks; this.users = users; this.access = access; this.members = members; this.notifications = notifications;
	}
	@Transactional
	public TaskResponse create(Long projectId, TaskRequest request, User actor) {
		Project project = access.requireManageable(projectId, actor);
		Task task = new Task(project, request.title().trim(), request.description());
		apply(task, request, project);
		Task saved = tasks.save(task); notifyAssignment(saved);
		return TaskResponse.from(saved);
	}
	@Transactional(readOnly = true)
	public Page<TaskResponse> list(Long projectId, TaskStatus status, String query, Pageable pageable, User actor) {
		access.requireAccessible(projectId, actor);
		Page<Task> result = query != null && !query.isBlank() ? tasks.search(projectId, query.trim(), pageable)
				: status != null ? tasks.findByProjectIdAndStatus(projectId, status, pageable)
				: tasks.findByProjectId(projectId, pageable);
		return result.map(TaskResponse::from);
	}
	@Transactional
	public TaskResponse update(Long id, TaskRequest request, User actor) {
		Task task = require(id); Project project = access.requireManageable(task.getProject().getId(), actor);
		task.setTitle(request.title().trim()); task.setDescription(request.description());
		apply(task, request, project); notifyAssignment(task); return TaskResponse.from(task);
	}
	@Transactional
	public TaskResponse updateStatus(Long id, TaskStatus status, User actor) {
		if (status == null) throw new IllegalArgumentException("Status is required");
		Task task = require(id); access.requireAccessible(task.getProject().getId(), actor);
		boolean canUpdate = actor.getRole() == Role.ADMIN || task.getProject().getOwner().getId().equals(actor.getId())
				|| task.getAssignee() != null && task.getAssignee().getId().equals(actor.getId());
		if (!canUpdate) throw new ForbiddenOperationException("Only the owner or assignee can update task status");
		task.setStatus(status); return TaskResponse.from(task);
	}
	@Transactional
	public void delete(Long id, User actor) {
		Task task = require(id); access.requireManageable(task.getProject().getId(), actor); tasks.delete(task);
	}
	public Task requireAccessible(Long id, User user) {
		Task task = require(id); access.requireAccessible(task.getProject().getId(), user); return task;
	}
	private Task require(Long id) { return tasks.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found")); }
	private void apply(Task task, TaskRequest request, Project project) {
		task.setPriority(request.priority() == null ? TaskPriority.MEDIUM : request.priority());
		task.setDueDate(request.dueDate());
		if (request.assigneeId() == null) { task.setAssignee(null); return; }
		if (!project.getOwner().getId().equals(request.assigneeId())
				&& !members.existsByProjectIdAndUserId(project.getId(), request.assigneeId())) {
			throw new IllegalArgumentException("Assignee must be a project member");
		}
		task.setAssignee(users.findById(request.assigneeId()).orElseThrow(() -> new ResourceNotFoundException("Assignee not found")));
	}
	private void notifyAssignment(Task task) {
		if (task.getAssignee() != null) notifications.notify(task.getAssignee(), "Task assigned: " + task.getTitle());
	}
}
