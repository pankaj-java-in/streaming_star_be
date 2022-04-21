package com.streaming.exception;

public class UserNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private final String message;
	public UserNotFoundException(String message) {
		this.message=message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
