package com.taskflow.backend.security;

import com.taskflow.backend.common.ResourceNotFoundException;
import com.taskflow.backend.user.User;
import com.taskflow.backend.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
	private final UserRepository users;

	public CurrentUserService(UserRepository users) { this.users = users; }

	public User require(Authentication authentication) {
		return users.findByEmailIgnoreCase(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists"));
	}
}
