package com.taskflow.backend.dashboard;

import com.taskflow.backend.notification.NotificationRepository;
import com.taskflow.backend.project.ProjectRepository;
import com.taskflow.backend.security.CurrentUserService;
import com.taskflow.backend.task.TaskRepository;
import com.taskflow.backend.task.TaskStatus;
import com.taskflow.backend.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
	public record DashboardResponse(long projectCount, long assignedTasks, long overdueTasks, long unreadNotifications) { }
	private final ProjectRepository projects; private final TaskRepository tasks;
	private final NotificationRepository notifications; private final CurrentUserService currentUser;
	public DashboardController(ProjectRepository projects, TaskRepository tasks, NotificationRepository notifications, CurrentUserService currentUser) {
		this.projects = projects; this.tasks = tasks; this.notifications = notifications; this.currentUser = currentUser;
	}
	@GetMapping
	DashboardResponse dashboard(Authentication auth) {
		User user = currentUser.require(auth);
		long projectCount = projects.findAccessible(user.getId(), Pageable.unpaged()).getTotalElements();
		long assigned = tasks.countByAssigneeIdAndStatusNot(user.getId(), TaskStatus.DONE);
		long overdue = tasks.findByAssigneeIdAndDueDateBeforeAndStatusNot(user.getId(), LocalDate.now(), TaskStatus.DONE).size();
		return new DashboardResponse(projectCount, assigned, overdue, notifications.countByUserIdAndIsReadFalse(user.getId()));
	}
}
