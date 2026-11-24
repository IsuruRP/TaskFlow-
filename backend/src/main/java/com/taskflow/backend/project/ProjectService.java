package com.taskflow.backend.project;

import com.taskflow.backend.common.ResourceNotFoundException;
import com.taskflow.backend.project.ProjectDtos.AddMemberRequest;
import com.taskflow.backend.project.ProjectDtos.MemberResponse;
import com.taskflow.backend.project.ProjectDtos.ProjectDetailResponse;
import com.taskflow.backend.project.ProjectDtos.ProjectRequest;
import com.taskflow.backend.project.ProjectDtos.ProjectResponse;
import com.taskflow.backend.user.User;
import com.taskflow.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
	private final ProjectRepository projects;
	private final ProjectMemberRepository members;
	private final UserRepository users;
	private final ProjectAccessService access;
	public ProjectService(ProjectRepository projects, ProjectMemberRepository members, UserRepository users,
			ProjectAccessService access) {
		this.projects = projects; this.members = members; this.users = users; this.access = access;
	}
	@Transactional
	public ProjectResponse create(ProjectRequest request, User owner) {
		validateDates(request);
		Project project = new Project(request.name().trim(), request.description(), owner, request.startDate(), request.dueDate());
		if (request.status() != null) project.setStatus(request.status());
		return ProjectResponse.from(projects.save(project));
	}
	@Transactional(readOnly = true)
	public Page<ProjectResponse> list(User user, String query, Pageable pageable) {
		Page<Project> result = query == null || query.isBlank()
				? projects.findAccessible(user.getId(), pageable)
				: projects.searchAccessible(user.getId(), query.trim(), pageable);
		return result.map(ProjectResponse::from);
	}
	@Transactional(readOnly = true)
	public ProjectDetailResponse get(Long id, User user) {
		Project project = access.requireAccessible(id, user);
		return new ProjectDetailResponse(ProjectResponse.from(project), memberList(project));
	}
	@Transactional
	public ProjectResponse update(Long id, ProjectRequest request, User user) {
		validateDates(request);
		Project project = access.requireManageable(id, user);
		project.setName(request.name().trim()); project.setDescription(request.description());
		project.setStartDate(request.startDate()); project.setDueDate(request.dueDate());
		if (request.status() != null) project.setStatus(request.status());
		return ProjectResponse.from(project);
	}
	@Transactional
	public void delete(Long id, User user) { projects.delete(access.requireManageable(id, user)); }
	@Transactional
	public MemberResponse addMember(Long id, AddMemberRequest request, User actor) {
		Project project = access.requireManageable(id, actor);
		if (request.userId() == null) throw new IllegalArgumentException("User id is required");
		if (members.existsByProjectIdAndUserId(id, request.userId()) || project.getOwner().getId().equals(request.userId())) {
			throw new IllegalArgumentException("User is already a project member");
		}
		User user = users.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return MemberResponse.from(members.save(new ProjectMember(project, user)));
	}
	@Transactional
	public void removeMember(Long projectId, Long userId, User actor) {
		access.requireManageable(projectId, actor);
		if (!members.existsByProjectIdAndUserId(projectId, userId)) throw new ResourceNotFoundException("Project member not found");
		members.deleteByProjectIdAndUserId(projectId, userId);
	}
	private List<MemberResponse> memberList(Project project) {
		List<MemberResponse> result = new java.util.ArrayList<>();
		result.add(new MemberResponse(project.getOwner().getId(), project.getOwner().getFullName(),
				project.getOwner().getEmail(), project.getCreatedAt()));
		result.addAll(members.findByProjectId(project.getId()).stream().map(MemberResponse::from).toList());
		return result;
	}
	private void validateDates(ProjectRequest request) {
		if (request.startDate() != null && request.dueDate() != null && request.dueDate().isBefore(request.startDate())) {
			throw new IllegalArgumentException("Due date cannot be before start date");
		}
	}
}
