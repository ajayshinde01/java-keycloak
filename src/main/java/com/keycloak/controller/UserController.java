package com.keycloak.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.keycloak.dto.ResetPasswordDTO;
import com.keycloak.dto.UserDTO;
import com.keycloak.service.UserService;
import com.keycloak.utils.ApiResponse;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

	
	@Autowired
	private UserService userService;
	
	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.resource}")
	private String clientId;

	@Value("${keycloak.credentials.secret}")
	private String clientSecret;
	
	@GetMapping("/get-all")
	public ResponseEntity<?> getAllUsers(){
		List<UserRepresentation> allUsers = userService.getAllUsers();
		return ResponseEntity.ok(allUsers);
	}
	
	@GetMapping("/search")
	public ResponseEntity<?> usersPage(@RequestParam(required = false,value = "") String keyword,@PageableDefault(page = 0,size = 5) Pageable pageable){
		Page<UserRepresentation> allUsers = userService.usersPage(keyword,pageable);
		return ResponseEntity.ok(allUsers);
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody UserDTO userDto){	
		UserDTO createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(createdUser);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") String id){
		UserDTO user = userService.getUserById(id);	
            return ResponseEntity.ok(user);
        
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUserById(@PathVariable("id") String id){
		String isDeleted=userService.deleteUserById(id);
		return ResponseEntity.ok(new ApiResponse(isDeleted, LocalDateTime.now()));
	}
	
	@PutMapping
	public ResponseEntity<?> updateUser(@RequestBody UserDTO userDto){
		UserDTO createdUser = userService.updateUser(userDto);
        return ResponseEntity.ok(createdUser);
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO passwordDto){
		userService.resetPassword(passwordDto);
		return ResponseEntity.ok(new ApiResponse("Password Updated", LocalDateTime.now()));
	}

}
