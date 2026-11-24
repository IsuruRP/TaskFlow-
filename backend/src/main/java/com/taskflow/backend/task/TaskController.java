package com.taskflow.backend.task;

import com.taskflow.backend.security.CurrentUserService;
import com.taskflow.backend.task.TaskDtos.TaskRequest;
import com.taskflow.backend.task.TaskDtos.TaskResponse;
import com.taskflow.backend.task.TaskDtos.TaskStatusRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskController {
	private final TaskService service; private final CurrentUserService currentUser;
	public TaskController(TaskService service, CurrentUserService currentUser) { this.service = service; this.currentUser = currentUser; }
	@PostMapping("/projects/{projectId}/tasks") @ResponseStatus(HttpStatus.CREATED)
	TaskResponse create(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request, Authentication auth) {
		return service.create(projectId, request, currentUser.require(auth));
	}
	@GetMapping("/projects/{projectId}/tasks")
	Page<TaskResponse> list(@PathVariable Long projectId, @RequestParam(required = false) TaskStatus status,
			@RequestParam(required = false) String query, Pageable pageable, Authentication auth) {
		return service.list(projectId, status, query, pageable, currentUser.require(auth));
	}
	@PutMapping("/tasks/{id}")
	TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest request, Authentication auth) {
		return service.update(id, request, currentUser.require(auth));
	}
	@PatchMapping("/tasks/{id}/status")
	TaskResponse status(@PathVariable Long id, @RequestBody TaskStatusRequest request, Authentication auth) {
		return service.updateStatus(id, request.status(), currentUser.require(auth));
	}
	@DeleteMapping("/tasks/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
	void delete(@PathVariable Long id, Authentication auth) { service.delete(id, currentUser.require(auth)); }
}
