package com.keycloak.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.keycloak.config.KeycloakConfig;
import com.keycloak.dto.RoleDTO;
import com.keycloak.exception.RoleNotFound;

import jakarta.ws.rs.ClientErrorException;

@Service

public class RoleService {

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

	public List<RoleRepresentation> getAllRealmRoles() {
		List<RoleRepresentation> realmRoles = keycloak.getKeycloak().realm(realm).roles().list();
		if(realmRoles.size()==0)
			throw  new RoleNotFound("Roles list is empty");
		Collections.sort(realmRoles, (role1, role2) -> role1.getName().compareToIgnoreCase(role2.getName()));
		return realmRoles;
	}
	
	public Page<RoleRepresentation> pageAllRealmRoles(String key,Pageable pageable) {
		 RolesResource rolesResource = keycloak.getKeycloak().realm(realm).roles();
		 List<RoleRepresentation> allRoles = rolesResource.list();
		    
		    List<RoleRepresentation> filteredRoles = new ArrayList<>();
		    for (RoleRepresentation role : allRoles) {
		        if (key != null && !key.isEmpty() &&( role.getName().toLowerCase().contains(key.toLowerCase())|| role.getDescription().toLowerCase().contains(key.toLowerCase()))) {
		            filteredRoles.add(role);
		        }
		    }
		    if(key == null || key.isEmpty()) {
		    	filteredRoles=allRoles;
		    }
		      
		    int totalFilteredRoles = filteredRoles.size();
	        int offset = pageable.getPageNumber() * pageable.getPageSize();
	        int size = pageable.getPageSize();
	        List<RoleRepresentation> rolesPage = new ArrayList<>();
	        for (int i = offset; i < Math.min(offset + size, totalFilteredRoles); i++) {
	            rolesPage.add(filteredRoles.get(i));
	        }
		    
	        
	        return new PageImpl<>(rolesPage, pageable, totalFilteredRoles);
	}

	public List<RoleRepresentation> getAllClientRoles() {
		List<RoleRepresentation> clientRoles = keycloak.getKeycloak().realm(realm).clients().get(numericClientId).roles()
				.list();
		if(clientRoles.size()==0)
			throw  new RoleNotFound("Roles list is empty");
		return clientRoles;
	}
	
	public Page<RoleRepresentation> pageAllClientRoles(String key,Pageable pageable) {
		 RolesResource rolesResource = keycloak.getKeycloak().realm(realm).clients().get(numericClientId).roles();
		 List<RoleRepresentation> allRoles = rolesResource.list();
		    
		    List<RoleRepresentation> filteredRoles = new ArrayList<>();
		    for (RoleRepresentation role : allRoles) {
		        if (key != null && !key.isEmpty() && role.getName().contains(key)) {
		            filteredRoles.add(role);
		        }
		    }
		    if(key == null || key.isEmpty()) {
		    	filteredRoles=allRoles;
		    }
		      
		    int totalFilteredRoles = filteredRoles.size();
	        int offset = pageable.getPageNumber() * pageable.getPageSize();
	        int size = pageable.getPageSize();
	        List<RoleRepresentation> rolesPage = new ArrayList<>();
	        for (int i = offset; i < Math.min(offset + size, totalFilteredRoles); i++) {
	            rolesPage.add(filteredRoles.get(i));
	        }
		    
	        
	        return new PageImpl<>(rolesPage, pageable, totalFilteredRoles);
	}

	public RoleDTO createRole(RoleDTO roleDto) {
		RoleRepresentation role = new RoleRepresentation();
		role.setName(roleDto.getName());
		role.setDescription(roleDto.getDescription());
		role.setComposite(roleDto.isComposite());
		role.setClientRole(roleDto.isClientRole());
		role.setContainerId(roleDto.getContainerId());
		
		try{if(roleDto.isClientRole()) {
			 keycloak.getKeycloak().realm(realm).clients().get(roleDto.getContainerId()).roles().create(role);
		}else {

		 keycloak.getKeycloak().realm(realm).roles().create(role);
		}
		RoleDTO createdRoleDto = new RoleDTO();
		createdRoleDto.setName(role.getName());
		createdRoleDto.setDescription(role.getDescription());
		createdRoleDto.setComposite(role.isComposite());
		createdRoleDto.setClientRole(role.getClientRole());
		createdRoleDto.setContainerId(role.getContainerId());
		createdRoleDto.setId(role.getId());
		return createdRoleDto;
		}catch (ClientErrorException ex) {
	        int statusCode = ex.getResponse().getStatus();
	        if (statusCode == 409) {
	        	throw new RoleNotFound("Role is already present");
	        }
		}
		return null;
	}

//	public RoleDTO createRole(RoleDTO roleDto) {
//		RoleRepresentation role = new RoleRepresentation();
//		role.setName(roleDto.getName());
//		role.setDescription(roleDto.getDescription());
//		
//		try{
//		 keycloak.getKeycloak().realm(realm).roles().create(role);
//		
//		RoleDTO createdRoleDto = new RoleDTO();
//		createdRoleDto.setName(role.getName());
//		createdRoleDto.setDescription(role.getDescription());
//		createdRoleDto.setId(role.getId());
//		return createdRoleDto;
//		}catch (ClientErrorException ex) {
//	        int statusCode = ex.getResponse().getStatus();
//	        if (statusCode == 409) {
//	        	throw new RoleNotFound("Role is already present");
//	        }
//		}
//		return null;
//	}
	
	public ResponseEntity<?> getRoleById(String id) throws RoleNotFound {
		List<RoleRepresentation> realmRolesResource = keycloak.getKeycloak().realm(realm).roles().list();
	    
		for (RoleRepresentation roleRepresentation : realmRolesResource) {
			if(roleRepresentation.getId().equals(id))
				return ResponseEntity.ok(roleRepresentation);
		}
	        List<RoleRepresentation> clientRolesResource = keycloak.getKeycloak().realm(realm).clients().get(numericClientId).roles().list();
	        for (RoleRepresentation roleRepresentation : clientRolesResource) {
				if(roleRepresentation.getId().equals(id))
					return ResponseEntity.ok(roleRepresentation);
			}		
	    throw new RoleNotFound("Role not found");
	}

	public ResponseEntity<?> updateRole(RoleDTO roleDto) {
		try {
			RoleResource roleResource=null;
			if(roleDto.isClientRole()) {
				roleResource=keycloak.getKeycloak().realm(realm).clients().get(numericClientId).roles().get(roleDto.getName());
			}else {
				 roleResource = keycloak.getKeycloak().realm(realm).roles().get(roleDto.getName());
				 
			}
			
			if (roleResource != null) {
		        RoleRepresentation updatedRole = new RoleRepresentation();
		        updatedRole.setName(roleDto.getName());
		        updatedRole.setDescription(roleDto.getDescription());
		        updatedRole.setComposite(roleDto.isComposite());
		        updatedRole.setClientRole(roleDto.isClientRole());
		        updatedRole.setContainerId(roleDto.getContainerId());

		        roleResource.update(updatedRole);

		        RoleDTO updatedRoleDto = new RoleDTO();
		        updatedRoleDto.setId(roleDto.getId());
		        updatedRoleDto.setName(updatedRole.getName());
		        updatedRoleDto.setDescription(updatedRole.getDescription());
		        updatedRoleDto.setComposite(updatedRole.isComposite());
		        updatedRoleDto.setClientRole(updatedRole.getClientRole());
		        updatedRoleDto.setContainerId(updatedRole.getContainerId());

		        return ResponseEntity.ok(updatedRoleDto);
			}
		    } catch (Exception e) {
		    	throw new RoleNotFound("Role not found");
			}
		return null;
	}

	public String deleteRole(String id) {
		
		RoleResource roleToDelete = null;
	    RolesResource rolesResource = keycloak.getKeycloak().realm(realm).roles();
	    List<RoleRepresentation> allRoles = rolesResource.list();
	    for (RoleRepresentation roleRep : allRoles) {
	        if (id.equals(roleRep.getId())) {
	            roleToDelete = rolesResource.get(roleRep.getName());
	            break;
	        }
	    }
	    
	    if (roleToDelete != null) {
	        roleToDelete.remove();
	        return "Deleted";
	    } else {
	        return "Role not found";
	    }
	}
	
	

}
