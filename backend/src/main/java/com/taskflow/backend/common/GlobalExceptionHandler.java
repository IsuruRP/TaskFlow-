package com.taskflow.backend.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ApiError> notFound(ResourceNotFoundException exception) {
		return response(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
	}

	@ExceptionHandler({ForbiddenOperationException.class})
	ResponseEntity<ApiError> forbidden(RuntimeException exception) {
		return response(HttpStatus.FORBIDDEN, exception.getMessage(), Map.of());
	}

	@ExceptionHandler({BadCredentialsException.class})
	ResponseEntity<ApiError> unauthorized(RuntimeException exception) {
		return response(HttpStatus.UNAUTHORIZED, "Invalid email or password", Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception) {
		Map<String, String> errors = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
		return response(HttpStatus.BAD_REQUEST, "Validation failed", errors);
	}

	@ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class})
	ResponseEntity<ApiError> badRequest(RuntimeException exception) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
	}

	private ResponseEntity<ApiError> response(HttpStatus status, String message, Map<String, String> errors) {
		return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), message, errors));
	}
}
