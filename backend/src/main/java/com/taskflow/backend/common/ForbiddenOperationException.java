package com.taskflow.backend.common;

public class ForbiddenOperationException extends RuntimeException {
	public ForbiddenOperationException(String message) { super(message); }
}
