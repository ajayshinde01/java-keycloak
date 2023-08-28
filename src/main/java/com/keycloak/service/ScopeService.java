package com.keycloak.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ResourceScopeResource;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.keycloak.config.KeycloakConfig;
import com.keycloak.dto.ScopeDTO;
import com.keycloak.exception.ScopeNotFound;
import com.keycloak.utils.ApiResponse;

import jakarta.ws.rs.core.Response;

@Service
public class ScopeService {

	@Autowired
	private KeycloakConfig keycloak;

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.resource}")
	private String clientId;

	@Value("${keycloak.credentials.secret}")
	private String clientSecret;

	@Value("${client-id}")
	private String numericClientId;

	public ResponseEntity<?> createScope(ScopeDTO scopeDto) {
		ClientResource clientResource = keycloak.getKeycloak().realm(realm).clients().get(numericClientId);
		ScopeRepresentation scopeRepresentation = new ScopeRepresentation();
		scopeRepresentation.setName(scopeDto.getName());
		scopeRepresentation.setDisplayName(scopeDto.getDisplayName());
		scopeRepresentation.setIconUri(scopeDto.getIconUri());
		try {
		ScopeRepresentation findByName = clientResource.authorization().scopes().findByName(scopeDto.getName());
		if(findByName!=null) {
			throw new ScopeNotFound("Scope already exists");
		}
		}catch(Exception e) {
			throw new ScopeNotFound(e.getMessage());
		}
		Response response = clientResource.authorization().scopes().create(scopeRepresentation);

		if (response.getStatus() == 201) {
			String scopeId = CreatedResponseUtil.getCreatedId(response);
			ScopeDTO scopeCreated = new ScopeDTO();
			scopeCreated.setId(scopeId);
			scopeCreated.setName(scopeDto.getName());
			scopeCreated.setDisplayName(scopeDto.getDisplayName());
			scopeCreated.setIconUri(scopeDto.getIconUri());
			return ResponseEntity.ok(scopeCreated);
		} else {
			return ResponseEntity.status(response.getStatus()).build();
		}
	}

	public ResponseEntity<?> getAllScope() {
		List<ScopeRepresentation> allScopes = keycloak.getKeycloak().realm(realm).clients().get(numericClientId)
				.authorization().scopes().scopes();
		if(allScopes.size()==0)
			throw  new ScopeNotFound("Scopes list is empty");
		return ResponseEntity.ok(allScopes);
	}

	public ResponseEntity<?> getScopeById(String id) {
		try {
			ResourceScopeResource scope = keycloak.getKeycloak().realm(realm).clients().get(numericClientId)
					.authorization().scopes().scope(id);
			return ResponseEntity.ok(scope.toRepresentation());
		} catch (Exception e) {
			throw new ScopeNotFound("Scope not found");
		}
	}

	public ResponseEntity<?> updateScope(ScopeDTO scopeDto) {
		try {
			ClientResource clientResource = keycloak.getKeycloak().realm(realm).clients().get(numericClientId);
			ResourceScopeResource scopeResource = clientResource.authorization().scopes().scope(scopeDto.getId());
			ScopeRepresentation scope=scopeResource.toRepresentation();
			scope.setName(scopeDto.getName());
			scope.setDisplayName(scopeDto.getDisplayName());
			scope.setIconUri(scopeDto.getIconUri());
			scopeResource.update(scope);
			return ResponseEntity.ok(scope);

		} catch (Exception e) {
			throw new ScopeNotFound("Scope not found");
		}
	}

	public ResponseEntity<?> deleteScope(String id) {
		try {
			ClientResource clientResource = keycloak.getKeycloak().realm(realm).clients().get(numericClientId);
			ResourceScopeResource scopeResource = clientResource.authorization().scopes().scope(id);
			scopeResource.remove();
			return ResponseEntity.ok(new ApiResponse("Scope deleted successfully",LocalDateTime.now()));

		} catch (Exception e) {
			throw new ScopeNotFound("Scope not found");
		}
	}

	
	public ResponseEntity<?> scopePage(String key, Pageable pageable) {
		List<ScopeRepresentation> allScopes = keycloak.getKeycloak().realm(realm).clients().get(numericClientId)
				.authorization().scopes().scopes();
		if(allScopes.size()==0)
			throw  new ScopeNotFound("Scopes list is empty");
		 List<ScopeRepresentation> filteredScopes = new ArrayList<>();
		    for (ScopeRepresentation scope : allScopes) {
		        if (key != null && !key.isEmpty() &&( scope.getName().toLowerCase().contains(key.toLowerCase()))) {
		        	filteredScopes.add(scope);
		        }
		    }
		    if(key == null || key.isEmpty()) {
		    	filteredScopes=allScopes;
		    }
		    
		    int totalFilteredScopes = filteredScopes.size();
	        int offset = pageable.getPageNumber() * pageable.getPageSize();
	        int size = pageable.getPageSize();
	        
	        List<ScopeRepresentation> scopesPage = new ArrayList<>();
	        for (int i = offset; i < Math.min(offset + size, totalFilteredScopes); i++) {
	        	scopesPage.add(filteredScopes.get(i));
	        }
		
		return ResponseEntity.ok(new PageImpl<>(scopesPage, pageable, totalFilteredScopes));
	}

	

}
