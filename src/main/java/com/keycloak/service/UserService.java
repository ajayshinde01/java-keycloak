package com.keycloak.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.keycloak.config.KeycloakConfig;
import com.keycloak.dto.ResetPasswordDTO;
import com.keycloak.dto.UserDTO;
import com.keycloak.exception.RoleNotFound;
import com.keycloak.exception.UserNotFound;

import jakarta.ws.rs.core.Response;

@Service
public class UserService {
	
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
	
	public List<UserRepresentation> getAllUsers(){
		List<UserRepresentation> allUsers = keycloak.getKeycloak().realm(realm).users().list();
		if(allUsers.size()==0)
			throw  new RoleNotFound("Users list is empty");
		return allUsers;
	}
	
	 public Page<UserRepresentation> usersPage(String key,Pageable pageable) {
	        UsersResource usersResource = keycloak.getKeycloak().realm(realm).users();

	        int offset = pageable.getPageNumber() * pageable.getPageSize();
	        int size = pageable.getPageSize();
	        
	        List<UserRepresentation> usersPage;

	        if (key != null && !key.isEmpty()) {
	            usersPage = usersResource.search(key, offset, size);
	        } else {
	            usersPage = usersResource.list(offset, size);
	        }

	        return new PageImpl<>(usersPage, pageable, usersResource.count());
	    }
	

	public UserDTO createUser(UserDTO userDto){
		
		UserRepresentation user = new UserRepresentation();
		user.setEnabled(true);
		user.setUsername(userDto.getEmail());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		user.setEnabled(userDto.isEnabled());

		RealmResource realmResource = keycloak.getKeycloak().realm(realm);
		UsersResource usersRessource = realmResource.users();
		
		Response response = usersRessource.create(user);

		userDto.setStatusCode(response.getStatus());
		userDto.setStatus(response.getStatusInfo().toString());

		if (response.getStatus() == 201) {

			String userId = CreatedResponseUtil.getCreatedId(response);
			CredentialRepresentation passwordCred = new CredentialRepresentation();
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			passwordCred.setValue(userDto.getPassword());
			UserResource userResource = usersRessource.get(userId);
			userResource.resetPassword(passwordCred);
			userDto.setId(userId);
			
			RoleRepresentation[] realmRoles = Arrays.stream(userDto.getRoles())
	                .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
	                .toArray(RoleRepresentation[]::new);

			userResource.roles().realmLevel().add(Arrays.asList(realmRoles));

		}
		if(response.getStatus()==409) {
			throw new UserNotFound("User name already present");
		}
		return userDto;
	}
	

	public UserDTO getUserById(String id){
		try {
		UserResource userResource = keycloak.getKeycloak().realm(realm).users().get(id);
		UserRepresentation user = userResource.toRepresentation();
		
		 List<RoleRepresentation> existingRoles = userResource.roles().realmLevel().listAll();
		 
		 String[] roles = new String[existingRoles.size()];
		 
		 for(int i=0;i<existingRoles.size();i++) {
			 roles[i]=existingRoles.get(i).getName();
		 }
		 
		 
		 UserDTO userToBeReturn = new UserDTO();
		 userToBeReturn.setId(user.getId());
		 userToBeReturn.setFirstName(user.getFirstName());
		 userToBeReturn.setLastName(user.getLastName());
		 userToBeReturn.setEmail(user.getEmail());
		 userToBeReturn.setEnabled(user.isEnabled());
		 userToBeReturn.setRoles(roles);
			return userToBeReturn;
		}catch (Exception e) {
			throw new UserNotFound("User not found");
		}
	
	}

	public String deleteUserById(String id) {
		UserResource user;
		UserRepresentation userToBeDelete;
	try {
		 user = keycloak.getKeycloak().realm(realm).users().get(id);
		 userToBeDelete=user.toRepresentation();
	}catch (Exception e) {
		throw new UserNotFound("User not found");
	}
		 
		if(userToBeDelete != null) {
			if(!userToBeDelete.isEnabled())
				throw new UserNotFound("User is already deleted");
		userToBeDelete.setEnabled(false);
		user.update(userToBeDelete);
		
		}	
		return "User deleted successfully";
	}


	public UserDTO updateUser(UserDTO userDto) {
		try {
			RealmResource realmResource = keycloak.getKeycloak().realm(realm);
			
			UserResource userToBeUpdated=keycloak.getKeycloak().realm(realm).users().get(userDto.getId());
			if (userToBeUpdated != null) {
		        UserRepresentation updatedUser = new UserRepresentation();
		        
		        updatedUser.setFirstName(userDto.getFirstName());
		        updatedUser.setLastName(userDto.getLastName());
		        updatedUser.setEmail(userDto.getEmail());
		        updatedUser.setEnabled(userDto.isEnabled());
		        
		        RoleRepresentation[] realmRoles = Arrays.stream(userDto.getRoles())
		                .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
		                .toArray(RoleRepresentation[]::new);
		       

		        userToBeUpdated.roles().realmLevel().add(Arrays.asList(realmRoles));
		      
		        userToBeUpdated.update(updatedUser);

		        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
		            CredentialRepresentation passwordCred = new CredentialRepresentation();
		            passwordCred.setTemporary(false);
		            passwordCred.setType(CredentialRepresentation.PASSWORD);
		            passwordCred.setValue(userDto.getPassword());
		            
		            userToBeUpdated.resetPassword(passwordCred);
		        }

		        UserDTO updatedUserDto = new UserDTO();
		        updatedUserDto.setId(userDto.getId());
		        updatedUserDto.setFirstName(updatedUser.getFirstName());
		        updatedUserDto.setLastName(updatedUser.getLastName());
		        updatedUserDto.setEmail(updatedUser.getEmail());
		        
		        return updatedUserDto;
			}
	    } catch (Exception e) {
			throw new UserNotFound("User not found");
		}
	        return null;   
	}

	public void resetPassword(ResetPasswordDTO passwordDto) {
		try {
		UserResource userToBeUpdated=keycloak.getKeycloak().realm(realm).users().get(passwordDto.getId());
		
			 CredentialRepresentation passwordCred = new CredentialRepresentation();
	            passwordCred.setTemporary(false);
	            passwordCred.setType(CredentialRepresentation.PASSWORD);
	            passwordCred.setValue(passwordDto.getNewPassword());
	            
	            userToBeUpdated.resetPassword(passwordCred);
		}catch (Exception e) {
			throw new UserNotFound("User not found");
		} 
		
	}
}
