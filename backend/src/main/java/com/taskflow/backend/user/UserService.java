package com.taskflow.backend.user;

import com.taskflow.backend.common.ResourceNotFoundException;
import com.taskflow.backend.user.UserDtos.UpdateProfileRequest;
import com.taskflow.backend.user.UserDtos.UpdateRoleRequest;
import com.taskflow.backend.user.UserDtos.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
	private final UserRepository users;

	public UserService(UserRepository users) { this.users = users; }

	@Transactional(readOnly = true)
	public Page<UserResponse> list(Pageable pageable) { return users.findAll(pageable).map(UserResponse::from); }

	@Transactional(readOnly = true)
	public List<UserResponse> directory(String query) {
		String value = query == null ? "" : query.trim();
		return users.findTop50ByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByFullName(value, value)
				.stream().filter(User::isEnabled).map(UserResponse::from).toList();
	}

	@Transactional
	public UserResponse updateProfile(User user, UpdateProfileRequest request) {
		user.setFullName(request.fullName().trim());
		return UserResponse.from(users.save(user));
	}

	@Transactional
	public UserResponse updateRole(Long id, UpdateRoleRequest request) {
		if (request.role() == null) throw new IllegalArgumentException("Role is required");
		User user = users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		user.setRole(request.role());
		return UserResponse.from(user);
	}

	@Transactional
	public UserResponse setEnabled(Long id, boolean enabled) {
		User user = users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		user.setEnabled(enabled);
		return UserResponse.from(user);
	}
}
