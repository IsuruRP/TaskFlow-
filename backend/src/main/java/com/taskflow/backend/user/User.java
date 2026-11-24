package com.taskflow.backend.user;

import com.taskflow.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

	@Column(nullable = false, length = 100)
	private String fullName;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Role role = Role.TEAM_MEMBER;

	@Column(nullable = false)
	private boolean enabled = true;

	protected User() { }

	public User(String fullName, String email, String passwordHash, Role role) {
		this.fullName = fullName;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
	}

	public String getFullName() { return fullName; }
	public void setFullName(String fullName) { this.fullName = fullName; }
	public String getEmail() { return email; }
	public String getPasswordHash() { return passwordHash; }
	public Role getRole() { return role; }
	public void setRole(Role role) { this.role = role; }
	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
