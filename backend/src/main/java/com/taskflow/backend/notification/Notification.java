package com.taskflow.backend.notification;

import com.taskflow.backend.user.User;
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
@Table(name = "notifications")
public class Notification {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private User user;
	private String message;
	private boolean isRead;
	private Instant createdAt = Instant.now();
	protected Notification() { }
	public Notification(User user, String message) { this.user = user; this.message = message; }
	public Long getId() { return id; }
	public User getUser() { return user; }
	public String getMessage() { return message; }
	public boolean isRead() { return isRead; }
	public void markRead() { isRead = true; }
	public Instant getCreatedAt() { return createdAt; }
}
