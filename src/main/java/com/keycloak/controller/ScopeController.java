package com.keycloak.controller;

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

import com.keycloak.dto.ScopeDTO;
import com.keycloak.service.ScopeService;

@RestController
@RequestMapping("/scope")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ScopeController {
	
	@Autowired
	private ScopeService scopeService;

	@PostMapping("/create")
	public ResponseEntity<?> createScope(@RequestBody ScopeDTO scopeDto){
		
		return scopeService.createScope(scopeDto);
	}
	
	@GetMapping("/get-all")
	public ResponseEntity<?> getAllScope(){
		return scopeService.getAllScope();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getScopeById(@PathVariable("id") String id){
		return scopeService.getScopeById(id);
	}
	
	@PutMapping
	public ResponseEntity<?> updateScope(@RequestBody ScopeDTO scopeDto){
		return scopeService.updateScope(scopeDto);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteScope(@PathVariable("id") String id){
		return scopeService.deleteScope(id);
	}
	
	@GetMapping("/search")
	public ResponseEntity<?> scopePage(@RequestParam(required = false,value = "") String keyword,@PageableDefault(page = 0,size = 5) Pageable pageable) {
		return scopeService.scopePage(keyword,pageable);
	}
	
}
