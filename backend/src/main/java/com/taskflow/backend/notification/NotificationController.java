package com.taskflow.backend.notification;

import com.taskflow.backend.notification.NotificationService.NotificationResponse;
import com.taskflow.backend.security.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
	private final NotificationService service; private final CurrentUserService currentUser;
	public NotificationController(NotificationService service, CurrentUserService currentUser) {
		this.service = service; this.currentUser = currentUser;
	}
	@GetMapping List<NotificationResponse> list(Authentication auth) { return service.list(currentUser.require(auth)); }
	@PatchMapping("/{id}/read") NotificationResponse read(@PathVariable Long id, Authentication auth) {
		return service.markRead(id, currentUser.require(auth));
	}
}
