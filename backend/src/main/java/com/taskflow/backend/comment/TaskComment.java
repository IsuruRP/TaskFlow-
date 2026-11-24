package com.taskflow.backend.comment;

import com.taskflow.backend.task.Task;
import com.taskflow.backend.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "task_comments")
public class TaskComment {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "task_id") private Task task;
	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "author_id") private User author;
	@Column(nullable = false, length = 2000) private String content;
	private Instant createdAt = Instant.now();
	protected TaskComment() { }
	public TaskComment(Task task, User author, String content) { this.task = task; this.author = author; this.content = content; }
	public Long getId() { return id; } public User getAuthor() { return author; }
	public String getContent() { return content; } public Instant getCreatedAt() { return createdAt; }
}
