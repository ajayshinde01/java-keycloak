package com.keycloak.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	private String id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private boolean isEnabled;
	private int statusCode;
	private String status;
	private String[] roles;
}