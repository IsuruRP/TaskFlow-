package com.taskflow.backend.project;

import com.taskflow.backend.project.ProjectDtos.AddMemberRequest;
import com.taskflow.backend.project.ProjectDtos.MemberResponse;
import com.taskflow.backend.project.ProjectDtos.ProjectDetailResponse;
import com.taskflow.backend.project.ProjectDtos.ProjectRequest;
import com.taskflow.backend.project.ProjectDtos.ProjectResponse;
import com.taskflow.backend.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
	private final ProjectService service; private final CurrentUserService currentUser;
	public ProjectController(ProjectService service, CurrentUserService currentUser) {
		this.service = service; this.currentUser = currentUser;
	}
	@PostMapping @ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
	ProjectResponse create(@Valid @RequestBody ProjectRequest request, Authentication auth) {
		return service.create(request, currentUser.require(auth));
	}
	@GetMapping
	Page<ProjectResponse> list(@RequestParam(required = false) String query, Pageable pageable, Authentication auth) {
		return service.list(currentUser.require(auth), query, pageable);
	}
	@GetMapping("/{id}")
	ProjectDetailResponse get(@PathVariable Long id, Authentication auth) {
		return service.get(id, currentUser.require(auth));
	}
	@PutMapping("/{id}")
	ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request, Authentication auth) {
		return service.update(id, request, currentUser.require(auth));
	}
	@DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
	void delete(@PathVariable Long id, Authentication auth) { service.delete(id, currentUser.require(auth)); }
	@PostMapping("/{id}/members") @ResponseStatus(HttpStatus.CREATED)
	MemberResponse addMember(@PathVariable Long id, @RequestBody AddMemberRequest request, Authentication auth) {
		return service.addMember(id, request, currentUser.require(auth));
	}
	@DeleteMapping("/{projectId}/members/{userId}") @ResponseStatus(HttpStatus.NO_CONTENT)
	void removeMember(@PathVariable Long projectId, @PathVariable Long userId, Authentication auth) {
		service.removeMember(projectId, userId, currentUser.require(auth));
	}
}
