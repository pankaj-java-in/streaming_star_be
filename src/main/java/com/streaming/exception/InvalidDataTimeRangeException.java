package com.streaming.exception;

public class InvalidDataTimeRangeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private final String message;
	public InvalidDataTimeRangeException(String message) {
		this.message=message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}

}
