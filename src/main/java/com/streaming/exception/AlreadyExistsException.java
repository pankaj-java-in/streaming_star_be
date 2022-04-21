package com.streaming.exception;

public class AlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final String message;
	
	public AlreadyExistsException(String message) {
		this.message=message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}

}
