package com.streaming.exception;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.streaming.utils.Response;

@ControllerAdvice
public class ApplicationExceptionHandler {
	
	@ExceptionHandler
	public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleItemNotFoundException(ItemNotFoundException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleInvalidDataTimeRangeException(InvalidDataTimeRangeException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleAlreadyExistsException(AlreadyExistsException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
	@ExceptionHandler
	public ResponseEntity<Object> handleInvalidMeetingUrlException(InvalidMeetingUrlException e, HttpServletRequest request) {
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, e.getMessage(), false);
	}
	
}
