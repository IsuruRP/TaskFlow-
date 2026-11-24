package com.taskflow.backend.security;

import com.taskflow.backend.user.Role;
import com.taskflow.backend.user.User;
import com.taskflow.backend.user.UserDtos.AuthResponse;
import com.taskflow.backend.user.UserDtos.LoginRequest;
import com.taskflow.backend.user.UserDtos.RegisterRequest;
import com.taskflow.backend.user.UserDtos.UserResponse;
import com.taskflow.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
public class AuthService {
	private final UserRepository users;
	private final PasswordEncoder passwordEncoder;
	private final JwtEncoder jwtEncoder;
	private final long expirationSeconds;

	public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder,
			@Value("${taskflow.jwt.expiration}") long expirationSeconds) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.jwtEncoder = jwtEncoder;
		this.expirationSeconds = expirationSeconds;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = request.email().trim().toLowerCase(Locale.ROOT);
		if (users.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Email is already registered");
		User user = users.save(new User(request.fullName().trim(), email,
				passwordEncoder.encode(request.password()), Role.TEAM_MEMBER));
		return tokenFor(user);
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		User user = users.findByEmailIgnoreCase(request.email())
				.filter(User::isEnabled)
				.orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new BadCredentialsException("Invalid credentials");
		}
		return tokenFor(user);
	}

	private AuthResponse tokenFor(User user) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("taskflow-api")
				.issuedAt(now)
				.expiresAt(now.plus(expirationSeconds, ChronoUnit.SECONDS))
				.subject(user.getEmail())
				.claim("userId", user.getId())
				.claim("name", user.getFullName())
				.claim("roles", List.of(user.getRole().name()))
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		return new AuthResponse(token, "Bearer", expirationSeconds, UserResponse.from(user));
	}
}
