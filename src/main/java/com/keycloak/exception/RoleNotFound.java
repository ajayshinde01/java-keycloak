package com.keycloak.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleNotFound extends RuntimeException {	

	private static final long serialVersionUID = 1L;
	private String message;
	private LocalDateTime dateTime;
	
	public RoleNotFound(String message) {
		super();
		this.message = message;
	}
	
	

}
