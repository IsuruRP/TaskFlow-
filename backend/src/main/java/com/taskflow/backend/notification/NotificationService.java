package com.taskflow.backend.notification;

import com.taskflow.backend.common.ForbiddenOperationException;
import com.taskflow.backend.common.ResourceNotFoundException;
import com.taskflow.backend.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {
	public record NotificationResponse(Long id, String message, boolean read, Instant createdAt) {
		static NotificationResponse from(Notification n) {
			return new NotificationResponse(n.getId(), n.getMessage(), n.isRead(), n.getCreatedAt());
		}
	}
	private final NotificationRepository notifications;
	public NotificationService(NotificationRepository notifications) { this.notifications = notifications; }
	public void notify(User user, String message) { notifications.save(new Notification(user, message)); }
	@Transactional(readOnly = true)
	public List<NotificationResponse> list(User user) {
		return notifications.findTop50ByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(NotificationResponse::from).toList();
	}
	@Transactional
	public NotificationResponse markRead(Long id, User user) {
		Notification n = notifications.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
		if (!n.getUser().getId().equals(user.getId())) throw new ForbiddenOperationException("Notification belongs to another user");
		n.markRead(); return NotificationResponse.from(n);
	}
}
