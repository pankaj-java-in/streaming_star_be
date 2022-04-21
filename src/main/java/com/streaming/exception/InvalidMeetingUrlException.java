package com.streaming.exception;

public class InvalidMeetingUrlException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final String message;
	public InvalidMeetingUrlException(String message) {
		this.message=message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
