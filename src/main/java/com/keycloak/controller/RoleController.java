package com.keycloak.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.management.relation.RoleNotFoundException;

import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.keycloak.dto.RoleDTO;
import com.keycloak.service.RoleService;
import com.keycloak.utils.ApiResponse;

@RestController
@RequestMapping("/role")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping("get-all-realm")
	public ResponseEntity<?> getAllRealmRoles() {
		List<RoleRepresentation> realmRoles = roleService.getAllRealmRoles();
		return ResponseEntity.ok(realmRoles);
	}
	
	@GetMapping("page-all-realm")
	public ResponseEntity<?> pageAllRealmRoles(@RequestParam(required = false,value = "") String keyword,@PageableDefault(page = 0,size = 5) Pageable pageable) {
		Page<RoleRepresentation> realmRoles = roleService.pageAllRealmRoles(keyword,pageable);
		return ResponseEntity.ok(realmRoles);
	}

	@GetMapping("get-all-client")
	public ResponseEntity<?> getAllClientRoles() {
		List<RoleRepresentation> clientRoles = roleService.getAllClientRoles();
		return ResponseEntity.ok(clientRoles);
	}
	
	@GetMapping("page-all-client")
	public ResponseEntity<?> pageAllClientRoles(@RequestParam(required = false,value = "") String keyword,@PageableDefault(page = 0,size = 5) Pageable pageable) {
		Page<RoleRepresentation> realmRoles = roleService.pageAllClientRoles(keyword,pageable);
		return ResponseEntity.ok(realmRoles);
	}

	@PostMapping("/create")
	public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDto) {
		RoleDTO roleCreated = roleService.createRole(roleDto);
		return ResponseEntity.ok(roleCreated);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getRoleById(@PathVariable("id") String id) throws RoleNotFoundException {
		return roleService.getRoleById(id);
	}

	@PutMapping
	public ResponseEntity<?> updateRole(@RequestBody RoleDTO roleDto) {
		return roleService.updateRole(roleDto);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRole(@PathVariable("id") String id){
		String isDeleted=roleService.deleteRole(id);
		return ResponseEntity.ok(new ApiResponse(isDeleted, LocalDateTime.now()));
	}
	

}
