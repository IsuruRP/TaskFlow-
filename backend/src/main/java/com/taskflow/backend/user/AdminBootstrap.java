package com.taskflow.backend.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
public class AdminBootstrap implements ApplicationRunner {
	private final UserRepository users;
	private final PasswordEncoder passwordEncoder;
	private final String email;
	private final String password;
	private final String name;

	public AdminBootstrap(UserRepository users, PasswordEncoder passwordEncoder,
			@Value("${taskflow.bootstrap.admin-email:}") String email,
			@Value("${taskflow.bootstrap.admin-password:}") String password,
			@Value("${taskflow.bootstrap.admin-name:TaskFlow Admin}") String name) {
		this.users = users; this.passwordEncoder = passwordEncoder;
		this.email = email; this.password = password; this.name = name;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (email.isBlank() || password.isBlank()) return;
		if (password.length() < 8) throw new IllegalArgumentException("ADMIN_PASSWORD must contain at least 8 characters");
		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
		if (!users.existsByEmailIgnoreCase(normalizedEmail)) {
			users.save(new User(name.trim(), normalizedEmail, passwordEncoder.encode(password), Role.ADMIN));
		}
	}
}
