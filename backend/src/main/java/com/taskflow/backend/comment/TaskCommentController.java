package com.taskflow.backend.comment;

import com.taskflow.backend.security.CurrentUserService;
import com.taskflow.backend.task.Task;
import com.taskflow.backend.task.TaskService;
import com.taskflow.backend.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class TaskCommentController {
	public record CommentRequest(@NotBlank @Size(max = 2000) String content) { }
	public record CommentResponse(Long id, Long authorId, String authorName, String content, Instant createdAt) {
		static CommentResponse from(TaskComment c) { return new CommentResponse(c.getId(), c.getAuthor().getId(), c.getAuthor().getFullName(), c.getContent(), c.getCreatedAt()); }
	}
	private final TaskCommentRepository comments; private final TaskService tasks; private final CurrentUserService currentUser;
	public TaskCommentController(TaskCommentRepository comments, TaskService tasks, CurrentUserService currentUser) {
		this.comments = comments; this.tasks = tasks; this.currentUser = currentUser;
	}
	@GetMapping @Transactional(readOnly = true)
	List<CommentResponse> list(@PathVariable Long taskId, Authentication auth) {
		tasks.requireAccessible(taskId, currentUser.require(auth));
		return comments.findByTaskIdOrderByCreatedAtAsc(taskId).stream().map(CommentResponse::from).toList();
	}
	@PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional
	CommentResponse add(@PathVariable Long taskId, @Valid @RequestBody CommentRequest request, Authentication auth) {
		User author = currentUser.require(auth); Task task = tasks.requireAccessible(taskId, author);
		return CommentResponse.from(comments.save(new TaskComment(task, author, request.content().trim())));
	}
}
