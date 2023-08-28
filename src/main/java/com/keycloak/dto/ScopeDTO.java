package com.keycloak.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScopeDTO {
	private String id;
	private String name;
	private String displayName;
	private String iconUri;
}