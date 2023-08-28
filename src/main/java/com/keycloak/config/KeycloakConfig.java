package com.keycloak.config;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfig {
	
	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;
	
	public Keycloak getKeycloak() {
		return KeycloakBuilder.builder().serverUrl(authServerUrl)
		            .grantType(OAuth2Constants.PASSWORD).realm("master")
		            .clientId("admin-cli").username("admin").password("admin")
		            .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
		            .build();
	}

}
