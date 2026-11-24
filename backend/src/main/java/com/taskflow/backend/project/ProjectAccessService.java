package com.taskflow.backend.project;

import com.taskflow.backend.common.ForbiddenOperationException;
import com.taskflow.backend.common.ResourceNotFoundException;
import com.taskflow.backend.user.Role;
import com.taskflow.backend.user.User;
import org.springframework.stereotype.Service;

@Service
public class ProjectAccessService {
	private final ProjectRepository projects;
	private final ProjectMemberRepository members;
	public ProjectAccessService(ProjectRepository projects, ProjectMemberRepository members) {
		this.projects = projects; this.members = members;
	}
	public Project requireAccessible(Long projectId, User user) {
		Project project = require(projectId);
		if (user.getRole() != Role.ADMIN && !isOwner(project, user)
				&& !members.existsByProjectIdAndUserId(projectId, user.getId())) {
			throw new ForbiddenOperationException("You do not have access to this project");
		}
		return project;
	}
	public Project requireManageable(Long projectId, User user) {
		Project project = require(projectId);
		if (user.getRole() != Role.ADMIN && !isOwner(project, user)) {
			throw new ForbiddenOperationException("Only the project owner can manage this project");
		}
		return project;
	}
	public Project require(Long id) {
		return projects.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}
	private boolean isOwner(Project project, User user) { return project.getOwner().getId().equals(user.getId()); }
}
