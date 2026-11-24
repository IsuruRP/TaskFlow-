package com.taskflow.backend.user;

import com.taskflow.backend.security.CurrentUserService;
import com.taskflow.backend.user.UserDtos.UpdateProfileRequest;
import com.taskflow.backend.user.UserDtos.UpdateRoleRequest;
import com.taskflow.backend.user.UserDtos.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;
	private final CurrentUserService currentUser;

	public UserController(UserService userService, CurrentUserService currentUser) {
		this.userService = userService;
		this.currentUser = currentUser;
	}

	@GetMapping("/me")
	UserResponse me(Authentication authentication) { return UserResponse.from(currentUser.require(authentication)); }

	@PutMapping("/me")
	UserResponse updateMe(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(currentUser.require(authentication), request);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	Page<UserResponse> list(Pageable pageable) { return userService.list(pageable); }

	@GetMapping("/directory")
	@PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
	List<UserResponse> directory(@RequestParam(required = false) String query) { return userService.directory(query); }

	@PatchMapping("/{id}/role")
	@PreAuthorize("hasRole('ADMIN')")
	UserResponse updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
		return userService.updateRole(id, request);
	}

	@PatchMapping("/{id}/enabled")
	@PreAuthorize("hasRole('ADMIN')")
	UserResponse setEnabled(@PathVariable Long id, @RequestParam boolean value) {
		return userService.setEnabled(id, value);
	}
}
