package com.example.demo.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", "Not Found");
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Bad Request");
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(fieldError ->
				errors.put(fieldError.getField(), fieldError.getDefaultMessage())
		);
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("errors", errors);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Object> handleExpiredJwt(ExpiredJwtException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.UNAUTHORIZED.value());
		body.put("error", "Unauthorized");
		body.put("message", "JWT token has expired");
		return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<Object> handleSignatureException(SignatureException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.UNAUTHORIZED.value());
		body.put("error", "Unauthorized");
		body.put("message", "JWT signature validation failed");
		return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAll(Exception ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", new Date());
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.put("error", "Internal Server Error");
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
