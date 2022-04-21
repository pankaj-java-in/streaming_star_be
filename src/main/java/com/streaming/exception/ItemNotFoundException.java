package com.streaming.exception;

public class ItemNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private final String message;
	public ItemNotFoundException(String message) {
		this.message=message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}

}
