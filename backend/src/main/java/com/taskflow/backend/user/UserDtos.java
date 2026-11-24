package com.taskflow.backend.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class UserDtos {
	private UserDtos() { }

	public record UserResponse(Long id, String fullName, String email, Role role, boolean enabled, Instant createdAt) {
		public static UserResponse from(User user) {
			return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(),
					user.isEnabled(), user.getCreatedAt());
		}
	}

	public record UpdateProfileRequest(@NotBlank @Size(max = 100) String fullName) { }
	public record UpdateRoleRequest(Role role) { }
	public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
	public record RegisterRequest(
			@NotBlank @Size(max = 100) String fullName,
			@NotBlank @Email @Size(max = 255) String email,
			@NotBlank @Size(min = 8, max = 72) String password) { }
	public record AuthResponse(String token, String tokenType, long expiresIn, UserResponse user) { }
}
