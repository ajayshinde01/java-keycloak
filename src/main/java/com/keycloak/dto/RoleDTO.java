package com.keycloak.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
	private String id;
	private String name;
	private String description;
	private boolean composite;
	private boolean clientRole;
	private String containerId;
}
