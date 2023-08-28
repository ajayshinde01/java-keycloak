package com.keycloak.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.keycloak.utils.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFound.class)
	public ResponseEntity<?> handleUserNotFoundException(UserNotFound userNotFound) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(userNotFound.getMessage());
		apiResponse.setDateTime(LocalDateTime.now());
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);

	}
	
	@ExceptionHandler(RoleNotFound.class)
	public ResponseEntity<?> handleRoleNotFoundException(RoleNotFound roleNotFound) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(roleNotFound.getMessage());
		apiResponse.setDateTime(LocalDateTime.now());
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(ScopeNotFound.class)
	public ResponseEntity<?> handleScopeNotFoundException(ScopeNotFound scopeNotFound) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(scopeNotFound.getMessage());
		apiResponse.setDateTime(LocalDateTime.now());
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);

	}
}